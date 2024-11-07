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
 *t 
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
    val highscore: StateFlow<Int>

    fun setGameType(gameType: GameType)
    fun saveSettings(newSettings: Settings)

    fun startGame()

    fun checkVisualMatch()
    fun checkAudioMatch()
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

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    private var job: Job? = null  // coroutine job for the game event

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var visualEvents = emptyArray<Int>()
    private var audioEvents = emptyArray<Int>()  // Array with all events

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun saveSettings(newSettings: Settings) {
        viewModelScope.launch {
            userPreferencesRepository.saveSettings(newSettings)
        }
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        _gameState.value = _gameState.value.copy(visualValue = -1, audioValue = -1, eventNr = 0, visualMatchStatus = MatchStatus.None, audioMatchStatus = MatchStatus.None, score = 0)

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> {
                    // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
                    audioEvents = nBackHelper.generateNBackString(settings.value.size, settings.value.audioCombinations, 30, settings.value.nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
                    Log.d("GameVM", "The following audio sequence was generated: ${audioEvents.contentToString()}")
                    runAudioGame(audioEvents)
                }
                GameType.AudioVisual -> {
                    // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
                    visualEvents = nBackHelper.generateNBackString(settings.value.size, settings.value.visualCombinations, 30, settings.value.nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
                    Log.d("GameVM", "The following visual sequence was generated: ${visualEvents.contentToString()}")
                    audioEvents = nBackHelper.generateNBackString(settings.value.size, settings.value.audioCombinations, 30, settings.value.nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
                    Log.d("GameVM", "The following audio sequence was generated: ${audioEvents.contentToString()}")
                    runAudioVisualGame(visualEvents, audioEvents)
                }
                GameType.Visual -> {
                    // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
                    visualEvents = nBackHelper.generateNBackString(settings.value.size, settings.value.visualCombinations, 30, settings.value.nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
                    Log.d("GameVM", "The following visual sequence was generated: ${visualEvents.contentToString()}")
                    runVisualGame(visualEvents)
                }
                GameType.None -> Unit
            }
            // Todo: update the highscore
            if (gameState.value.score > _highscore.value) {
                userPreferencesRepository.saveHighScore(_gameState.value.score)
            }
            _gameState.value = _gameState.value.copy(gameType = GameType.None)
        }
    }

    override fun checkVisualMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        val nBackIndex = gameState.value.eventNr - settings.value.nBack - 1
        if (gameState.value.visualMatchStatus == MatchStatus.None) {
            if (nBackIndex >= 0 && gameState.value.visualValue == visualEvents[nBackIndex]) {
                _gameState.value = _gameState.value.copy(visualMatchStatus = MatchStatus.Correct, score = gameState.value.score + 1)
            }
            else {
                _gameState.value = _gameState.value.copy(visualMatchStatus = MatchStatus.Wrong)
            }
        }
    }

    override fun checkAudioMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        val nBackIndex = gameState.value.eventNr - settings.value.nBack - 1
        if (gameState.value.audioMatchStatus == MatchStatus.None) {
            if (nBackIndex >= 0 && gameState.value.audioValue == audioEvents[nBackIndex]) {
                _gameState.value = _gameState.value.copy(audioMatchStatus = MatchStatus.Correct, score = gameState.value.score + 1)
            }
            else {
                _gameState.value = _gameState.value.copy(audioMatchStatus = MatchStatus.Wrong)
            }
        }
    }

    private suspend fun runAudioGame(events: Array<Int>) {
        // Todo: Make work for Basic grade
        for (value in events) {
            _gameState.value = _gameState.value.copy(audioValue = value, eventNr = _gameState.value.eventNr + 1, audioMatchStatus = MatchStatus.None)
            delay(settings.value.eventInterval)
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        // Todo: Replace this code for actual game code
        for (value in events) {
            _gameState.value = _gameState.value.copy(visualValue = value, eventNr = _gameState.value.eventNr + 1, visualMatchStatus = MatchStatus.None)
            delay(settings.value.eventInterval)
        }

    }

    private suspend fun runAudioVisualGame(visualEvents: Array<Int>, audioEvents: Array<Int>){
        // Todo: Make work for Higher grade
        for (i in 0 until minOf(visualEvents.size, audioEvents.size)) {
            _gameState.value = _gameState.value.copy(visualValue = visualEvents[i], audioValue = audioEvents[i], eventNr = _gameState.value.eventNr + 1, visualMatchStatus = MatchStatus.None, audioMatchStatus = MatchStatus.None)
            delay(settings.value.eventInterval)
        }
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
        viewModelScope.launch {
            userPreferencesRepository.size.collect {
                _settings.value = _settings.value.copy(size = it)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.eventInterval.collect {
                _settings.value = _settings.value.copy(eventInterval = it)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.nBack.collect {
                _settings.value = _settings.value.copy(nBack = it)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.visualCombinations.collect {
                _settings.value = _settings.value.copy(visualCombinations = it)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.audioCombinations.collect {
                _settings.value = _settings.value.copy(audioCombinations = it)
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
    val visualValue: Int = -1,  // The value of the array string
    val audioValue: Int = -1,
    val eventNr: Int = 0,
    val visualMatchStatus: MatchStatus = MatchStatus.None,
    val audioMatchStatus: MatchStatus = MatchStatus.None,
    val score: Int = 0
)