package com.tatav.pomodoro

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {
    private val _state = MutableStateFlow(PomodoroState(
        timeRemainingSeconds = 25 * 60 // Start with work duration
    ))
    val state: StateFlow<PomodoroState> = _state.asStateFlow()
    private var timerJob: Job? = null

    fun updateSettings(settings: PomodoroSettings) {
        _state.update { currentState ->
            currentState.copy(
                settings = settings,
                timeRemainingSeconds = getCurrentDurationInSeconds(
                    currentState.currentTimerType,
                    settings
                )
            )
        }
    }

    fun startTimer() {
        if (_state.value.timerState == TimerState.RUNNING) return

        if (_state.value.timerState == TimerState.IDLE) {
            resetTimer()
        }

        _state.update { it.copy(timerState = TimerState.RUNNING) }

        timerJob = viewModelScope.launch {
            while (_state.value.timeRemainingSeconds > 0 && _state.value.timerState == TimerState.RUNNING) {
                delay(1000)
                _state.update { it.copy(timeRemainingSeconds = it.timeRemainingSeconds - 1) }
            }

            if (_state.value.timeRemainingSeconds <= 0) {
                _state.update { it.copy(timerState = TimerState.FINISHED) }
                handleTimerCompletion()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _state.update { it.copy(timerState = TimerState.PAUSED) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _state.update { currentState ->
            currentState.copy(
                timerState = TimerState.IDLE,
                timeRemainingSeconds = getCurrentDurationInSeconds(
                    currentState.currentTimerType,
                    currentState.settings
                )
            )
        }
    }

    fun skipToNext() {
        timerJob?.cancel()
        advanceToNextTimer()
    }

    private fun handleTimerCompletion() {
        advanceToNextTimer()
    }

    private fun advanceToNextTimer() {
        val currentState = _state.value

        when (currentState.currentTimerType) {
            TimerType.WORK -> {
                val completedPomodoros = currentState.completedPomodoros + 1
                val nextTimerType = if (completedPomodoros % currentState.settings.pomodorosUntilLongBreak == 0) {
                    TimerType.LONG_BREAK
                } else {
                    TimerType.SHORT_BREAK
                }

                _state.update { it.copy(
                    timerState = TimerState.IDLE,
                    currentTimerType = nextTimerType,
                    timeRemainingSeconds = getCurrentDurationInSeconds(nextTimerType, currentState.settings),
                    completedPomodoros = completedPomodoros
                )}
            }
            TimerType.SHORT_BREAK, TimerType.LONG_BREAK -> {
                _state.update { it.copy(
                    timerState = TimerState.IDLE,
                    currentTimerType = TimerType.WORK,
                    timeRemainingSeconds = getCurrentDurationInSeconds(TimerType.WORK, currentState.settings)
                )}
            }
        }
    }

    private fun getCurrentDurationInSeconds(
        timerType: TimerType,
        settings: PomodoroSettings
    ): Int {
        return when (timerType) {
            TimerType.WORK -> settings.workDurationMinutes * 60
            TimerType.SHORT_BREAK -> settings.shortBreakDurationMinutes * 60
            TimerType.LONG_BREAK -> settings.longBreakDurationMinutes * 60
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}