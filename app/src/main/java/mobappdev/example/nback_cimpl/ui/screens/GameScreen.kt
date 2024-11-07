package mobappdev.example.nback_cimpl.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.ui.viewmodels.MatchStatus
import kotlin.math.sqrt

@Composable
fun GameScreen(
    vm: GameViewModel,
    navController: NavController,
    textToSpeech: TextToSpeech
) {
    val gameState by vm.gameState.collectAsState()
    val settings by vm.settings.collectAsState()
    var showGameOver by remember { mutableStateOf(false) }
    var eventColor by remember { mutableStateOf(Color.LightGray) }

    LaunchedEffect(gameState.gameType) {
        if (gameState.gameType == GameType.None) {
            showGameOver = true
        }
        else {
            vm.startGame()
        }
    }

    LaunchedEffect(gameState.eventNr) {
        if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
            Log.d("GameScreen", (gameState.audioValue - 1 + 'A'.code).toChar().toString())
            textToSpeech.speak((gameState.audioValue - 1 + 'A'.code).toChar().toString(), TextToSpeech.QUEUE_FLUSH, null, null )
        }
        if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
            eventColor = Color.DarkGray
            delay(settings.eventInterval - 250L)
            eventColor = Color.LightGray
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Score: ${gameState.score}",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Event: ${gameState.eventNr}",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Box(
                modifier = Modifier
                    .height(400.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                ){
                    val gridDimension = sqrt(settings.visualCombinations.toDouble()).toInt()
                    repeat(gridDimension) {row ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            repeat(gridDimension) { col ->
                                var background = Color.LightGray
                                if ((gameState.gameType == GameType.Visual ||
                                    gameState.gameType == GameType.AudioVisual) &&
                                    col + 1 + gridDimension * row == gameState.visualValue) {
                                    background = eventColor
                                }
                                Box(
                                    modifier = Modifier
                                        .size((300 / gridDimension).dp)
                                        .background(background)
                                )

                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    enabled = when (gameState.gameType) {
                        GameType.Audio, GameType.AudioVisual -> true
                        GameType.Visual, GameType.None -> false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = when (gameState.audioMatchStatus) {
                        MatchStatus.None -> Color.DarkGray
                        MatchStatus.Correct -> Color.Green
                        MatchStatus.Wrong -> Color.Red
                    }),
                    onClick = {
                    // Todo: change this button behaviour
                    if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
                        vm.checkAudioMatch()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
                Button(
                    enabled = when (gameState.gameType) {
                        GameType.Visual, GameType.AudioVisual -> true
                        GameType.Audio, GameType.None -> false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = when (gameState.visualMatchStatus) {
                        MatchStatus.None -> Color.DarkGray
                        MatchStatus.Correct -> Color.Green
                        MatchStatus.Wrong -> Color.Red
                    }),
                    onClick = {
                        // Todo: change this button behaviour
                        if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
                            vm.checkVisualMatch()
                        }
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
            }
        }
    }

    if (showGameOver) {
        Dialog(onDismissRequest = {
            showGameOver = false
            navController.navigate("home")
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Game over",
                        style = TextStyle(fontSize = 25.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Score: " + gameState.score
                    )
                }
            }
        }
    }
}