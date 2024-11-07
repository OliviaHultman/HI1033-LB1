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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
    var eventActive by remember { mutableStateOf(false) }
    var gameStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.startGame()
    }

    LaunchedEffect(gameState.finished) {
        if (gameStarted && gameState.finished) {
            showGameOver = true
        }
        else if (!gameState.finished) {
            gameStarted = true
        }
    }

    LaunchedEffect(gameState.eventNr) {
        if (!gameState.finished) {
            if (settings.gameType == GameType.Audio || settings.gameType == GameType.AudioVisual) {
                Log.d("GameScreen", (gameState.audioValue - 1 + 'A'.code).toChar().toString())
                textToSpeech.speak(
                    (gameState.audioValue - 1 + 'A'.code).toChar().toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
            if (settings.gameType == GameType.Visual || settings.gameType == GameType.AudioVisual) {
                eventActive = true
                delay(settings.eventInterval - 250L)
                eventActive = false
            }
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
                    modifier = Modifier.padding(25.dp),
                    text = "Score: ${gameState.score}",
                    style = TextStyle(fontSize = 25.sp)
                )
                Text(
                    modifier = Modifier.padding(25.dp),
                    text = "Event: ${gameState.eventNr}",
                    style = TextStyle(fontSize = 25.sp)
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
                                if ((settings.gameType == GameType.Visual ||
                                    settings.gameType == GameType.AudioVisual) &&
                                    col + 1 + gridDimension * row == gameState.visualValue &&
                                    eventActive) {
                                    background = MaterialTheme.colorScheme.primary
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
                    enabled = when (settings.gameType) {
                        GameType.Audio, GameType.AudioVisual -> true
                        GameType.Visual, GameType.None -> false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = when (gameState.audioMatchStatus) {
                        MatchStatus.None -> MaterialTheme.colorScheme.primary
                        MatchStatus.Correct -> Color.Green
                        MatchStatus.Wrong -> Color.Red
                    }),
                    onClick = {
                        if (settings.gameType == GameType.Audio || settings.gameType == GameType.AudioVisual) {
                            vm.checkAudioMatch()
                        }
                    },
                    modifier = Modifier.height(100.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Audio match",
                            style = TextStyle(fontSize = 12.5.sp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.sound_on),
                            contentDescription = "Sound",
                            modifier = Modifier
                                .height(48.dp)
                                .aspectRatio(3f / 2f)
                        )
                    }
                }
                Button(
                    enabled = when (settings.gameType) {
                        GameType.Visual, GameType.AudioVisual -> true
                        GameType.Audio, GameType.None -> false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = when (gameState.visualMatchStatus) {
                        MatchStatus.None -> MaterialTheme.colorScheme.primary
                        MatchStatus.Correct -> Color.Green
                        MatchStatus.Wrong -> Color.Red
                    }),
                    onClick = {
                        if (settings.gameType == GameType.Visual || settings.gameType == GameType.AudioVisual) {
                            vm.checkVisualMatch()
                        }
                    },
                    modifier = Modifier.height(100.dp)

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Visual match",
                            style = TextStyle(fontSize = 12.5.sp)
                        )
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
    }

    if (showGameOver) {
        Dialog(
            onDismissRequest = {  }
        ) {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showGameOver = false
                            navController.navigate("home")
                        },
                        modifier = Modifier.width(125.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Home")
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier
                                    .height(24.dp)
                                    .aspectRatio(3f / 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}