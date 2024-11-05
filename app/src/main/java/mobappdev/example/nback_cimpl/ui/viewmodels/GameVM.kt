package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val settings: StateFlow<Settings>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun setSize(size: Int)
    fun setEventInterval(eventInterval: Long)
    fun setNBack(nBack: Int)
    fun setVisualCombinations(visualCombinations: Int)
    fun setAudioCombinations(audioCombinations: Int)
    fun startGame()

    fun checkMatch()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _settings = MutableStateFlow(Settings())
    override val settings: StateFlow<Settings>
        get() = _settings.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 1

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun setSize(size: Int) {
        _settings.value = _settings.value.copy(size = size)
    }

    override fun setEventInterval(eventInterval: Long) {
        _settings.value = _settings.value.copy(eventInterval = eventInterval)
    }

    override fun setNBack(nBack: Int) {
        _settings.value = _settings.value.copy(nBack = nBack)
    }

    override fun setVisualCombinations(visualCombinations: Int) {
        _settings.value = _settings.value.copy(visualCombinations = visualCombinations)
    }

    override fun setAudioCombinations(audioCombinations: Int) {
        _settings.value = _settings.value.copy(audioCombinations = audioCombinations)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(30, 9, 30, nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")

        _score.value = 0;
        _gameState.value = _gameState.value.copy(eventValue = -1, eventNr = 0, matchStatus = MatchStatus.None)

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(events)
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
                GameType.None -> Unit
            }
            // Todo: update the highscore
            if (_score.value > _highscore.value) {
                _highscore.value = _score.value
                userPreferencesRepository.saveHighScore(_score.value)
            }
            _gameState.value = _gameState.value.copy(gameType = GameType.None)
        }
    }

    override fun checkMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        if (gameState.value.matchStatus == MatchStatus.None) {
            val eventNr = gameState.value.eventNr
            if (eventNr - nBack > 0 && gameState.value.eventValue == events[eventNr - nBack - 1]) {
                _score.value++
                _gameState.value = _gameState.value.copy(matchStatus = MatchStatus.Correct)
            } else {
                _gameState.value = _gameState.value.copy(matchStatus = MatchStatus.Wrong)
            }
        }
    }
    private suspend fun runAudioGame(events: Array<Int>) {
        // Todo: Make work for Basic grade
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = value, eventNr = _gameState.value.eventNr + 1, matchStatus = MatchStatus.None)
            delay(eventInterval)
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        // Todo: Replace this code for actual game code
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = value, eventNr = _gameState.value.eventNr + 1, matchStatus = MatchStatus.None)
            delay(eventInterval)
        }

    }

    private fun runAudioVisualGame(){
        // Todo: Make work for Higher grade
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual,
    None
}

enum class MatchStatus{
    None,
    Correct,
    Wrong
}

data class Settings(
    val size: Int = 10,
    val eventInterval: Long = 2000L,
    val nBack: Int = 2,
    val visualCombinations: Int = 9,
    val audioCombinations: Int = 9,

)

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1,  // The value of the array string
    val eventNr: Int = 0,
    val matchStatus: MatchStatus = MatchStatus.None,
)