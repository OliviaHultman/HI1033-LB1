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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
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
    var showDialog by remember { mutableStateOf(false) }

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
                Text(
                    modifier = Modifier.padding(32.dp),
                    text = "N-back game",
                    style = MaterialTheme.typography.headlineLarge
                )
                Button(
                    onClick = {
                        showDialog = true;
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f),
                    )
                }
            }
            Text(
                modifier = Modifier.padding(32.dp),
                text = "High score: $highscore",
                style = MaterialTheme.typography.headlineLarge
            )
            // Todo: You'll probably want to change this "BOX" part of the composable
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    modifier = Modifier.width(150.dp),
                    onClick = {
                    // Todo: change this button behaviour
                    vm.setGameType(GameType.Audio);
                    navController.navigate("game");
                }) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sound")
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
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        // Todo: change this button behaviour
                        vm.setGameType(GameType.Visual);
                        navController.navigate("game");
                    }) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Visual")
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.width(250.dp),
                    onClick = {
                        vm.setGameType(GameType.AudioVisual);
                        navController.navigate("game");
                    }) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Combination")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.sound_on),
                                contentDescription = "Sound",
                                modifier = Modifier
                                    .height(48.dp)
                                    .aspectRatio(3f / 2f)
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
    }

    if (showDialog) {

        Dialog(onDismissRequest = { showDialog = false }) {
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

                    Text("Nr. of events: ${newSettings.size}")
                    Slider(
                        value = newSettings.size.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(size = it.toInt()) },
                        valueRange = 2f..100f,
                        steps = 97,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Event interval: ${newSettings.eventInterval} ms")
                    Slider(
                        value = newSettings.eventInterval.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(eventInterval = it.toLong()) },
                        valueRange = 500f..3000f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("N-back: ${newSettings.nBack}")
                    Slider(
                        value = newSettings.nBack.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(nBack = it.toInt()) },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val gridDimension = sqrt(newSettings.visualCombinations.toDouble())
                    Text("Visual grid dimension: ${gridDimension.toInt()}")
                    Slider(
                        value = gridDimension.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(visualCombinations = it.pow(2).toInt()) },
                        valueRange = 2f..10f,
                        steps = 7,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Nr. of audio letters: ${newSettings.audioCombinations}")
                    Slider(
                        value = newSettings.audioCombinations.toFloat(),
                        onValueChange = { newSettings = newSettings.copy(audioCombinations = it.toInt()) },
                        valueRange = 2f..26f,
                        steps = 23,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            vm.saveSettings(newSettings)
                            showDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}