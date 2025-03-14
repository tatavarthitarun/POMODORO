package com.tatav.pomodoro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform