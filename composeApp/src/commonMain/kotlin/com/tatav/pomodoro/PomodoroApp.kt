package com.tatav.pomodoro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun PomodoroApp() {
    val viewModel = getViewModel(
        key = "pomodoro-view-model",
        factory = viewModelFactory { PomodoroViewModel() }
    )

    val state by viewModel.state.collectAsState<PomodoroState>()
    MaterialTheme {
        PomodoroScreen(
            state = state,
            onStartClicked = viewModel::startTimer,
            onPauseClicked = viewModel::pauseTimer,
            onResetClicked = viewModel::resetTimer,
            onSkipClicked = viewModel::skipToNext
        )
    }
}

@Composable
fun PomodoroScreen(
    state: PomodoroState,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // Timer type indicator
        Text(
            text = when (state.currentTimerType) {
                TimerType.WORK -> "Focus Time"
                TimerType.SHORT_BREAK -> "Short Break"
                TimerType.LONG_BREAK -> "Long Break"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        // Timer display
        TimerDisplay(state.timeRemainingSeconds)

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            if (state.timerState == TimerState.RUNNING) {
                Button(onClick = onPauseClicked) {
                    Text("Pause")
                }
            } else {
                Button(onClick = onStartClicked) {
                    Text("Start")
                }
            }

            Button(onClick = onResetClicked) {
                Text("Reset")
            }

            Button(onClick = onSkipClicked) {
                Text("Skip")
            }
        }

        // Progress indicator
        Text(
            text = "Completed Pomodoros: ${state.completedPomodoros}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun TimerDisplay(timeRemainingSeconds: Int) {
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60

    Text(
        text = String.format("%02d:%02d", minutes, seconds),
        fontSize = 64.sp
    )
}