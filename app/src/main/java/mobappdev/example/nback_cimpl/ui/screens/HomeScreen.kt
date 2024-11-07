package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    val settings by vm.settings.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        showSettings = true;
                    }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f),
                    )
                }
            }
            Text (
                modifier = Modifier.padding(vertical = 75.dp),
                text = "N-back game",
                style = TextStyle(fontSize = 50.sp)
            )
            Text(
                modifier = Modifier.padding(vertical = 25.dp),
                text = "High score: $highscore",
                style = TextStyle(fontSize = 25.sp)
            )
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Row() {
                        Text(
                            text = "Game type: ",
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = when (settings.gameType) {
                                GameType.Visual, GameType.Audio -> settings.gameType.toString()
                                GameType.AudioVisual -> "Combination"
                                GameType.None -> ""
                            },
                            modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row() {
                        Text(
                            text = "Nr. of events: ",
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = settings.size.toString(),
                            modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row() {
                        Text(
                            text = "Event interval: ",
                            modifier = Modifier.weight(2f))
                        Text(
                            text = (settings.eventInterval / 1000.0).toString() + " s",
                            modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row() {
                        Text(
                            text = "N-back: ",
                            modifier = Modifier.weight(2f))
                        Text(
                            text = settings.nBack.toString(),
                            modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row() {
                        Text(
                            text = "Visual grid dimension: ",
                            modifier = Modifier.weight(2f))
                        Text(
                            text = sqrt(settings.visualCombinations.toDouble()).toInt().toString(),
                            modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row() {
                        Text(
                            text = "Nr. of audio letters: ",
                            modifier = Modifier.weight(2f))
                        Text(
                            text = settings.audioCombinations.toString(),
                            modifier = Modifier.weight(1f))
                    }
                    }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .width(250.dp)
                        .height(100.dp),
                    onClick = {
                        vm.newGame()
                        navController.navigate("game");
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Start game",
                                style = TextStyle(fontSize = 25.sp)
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play arrow",
                                modifier = Modifier
                                    .height(48.dp)
                                    .aspectRatio(3f / 2f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {

        Dialog(onDismissRequest = { showSettings = false }) {
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
                    var newSettings by remember { mutableStateOf(settings) }

                    Text("Game type:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = newSettings.gameType == GameType.Visual,
                            onClick = { newSettings = newSettings.copy(gameType = GameType.Visual) }
                        )
                        Text("Visual")
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        RadioButton(
                            selected = newSettings.gameType == GameType.Audio,
                            onClick = { newSettings = newSettings.copy(gameType = GameType.Audio) }
                        )
                        Text("Audio")
                        }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = newSettings.gameType == GameType.AudioVisual,
                            onClick = { newSettings = newSettings.copy(gameType = GameType.AudioVisual) }
                        )
                        Text("Combination")
                    }

                    Text("Nr. of events: ${newSettings.size}")
                    Slider(
                        value = newSettings.size.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(size = it.toInt()) },
                        valueRange = 2f..100f,
                        steps = 97,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Event interval: ${newSettings.eventInterval / 1000.0} s")
                    Slider(
                        value = newSettings.eventInterval.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(eventInterval = it.toLong()) },
                        valueRange = 500f..3000f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("N-back: ${newSettings.nBack}")
                    Slider(
                        value = newSettings.nBack.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(nBack = it.toInt()) },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val gridDimension = sqrt(newSettings.visualCombinations.toDouble())
                    Text("Visual grid dimension: ${gridDimension.toInt()}")
                    Slider(
                        value = gridDimension.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(visualCombinations = it.pow(2).toInt()) },
                        valueRange = 2f..10f,
                        steps = 7,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Nr. of audio letters: ${newSettings.audioCombinations}")
                    Slider(
                        value = newSettings.audioCombinations.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(audioCombinations = it.toInt()) },
                        valueRange = 2f..26f,
                        steps = 23,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            vm.saveSettings(newSettings)
                            showSettings = false
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}