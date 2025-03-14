package com.tatav.pomodoro


enum class TimerState {
    IDLE, RUNNING, PAUSED, FINISHED
}

enum class TimerType {
    WORK, SHORT_BREAK, LONG_BREAK
}

data class PomodoroSettings(
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val pomodorosUntilLongBreak: Int = 4
)

data class PomodoroState(
    val timerState: TimerState = TimerState.IDLE,
    val currentTimerType: TimerType = TimerType.WORK,
    val timeRemainingSeconds: Int = 0,
    val completedPomodoros: Int = 0,
    val settings: PomodoroSettings = PomodoroSettings()
)