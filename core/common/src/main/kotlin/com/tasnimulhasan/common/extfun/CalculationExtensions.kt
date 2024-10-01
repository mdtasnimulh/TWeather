package com.tasnimulhasan.common.extfun

fun calculateProgressBySunriseSunset(sunrise: Long, sunset: Long): Int {
    val currentTime = System.currentTimeMillis() / 1000
    val totalTime = sunset - sunrise
    val elapsedTime = currentTime - sunrise
    return when {
        currentTime < sunrise -> 0 // Before sunrise
        currentTime > sunset -> totalTime.toInt() // After sunset, full progress
        else -> elapsedTime.toInt() // During the day, normal progress
    }
}

fun calculateRemainingTime(sunrise: Long, sunset: Long): Long {
    val currentTime = System.currentTimeMillis() / 1000
    return when {
        currentTime < sunrise -> { sunrise - currentTime }
        currentTime > sunset -> {
            val nextDaySunrise = sunrise + 86400
            nextDaySunrise - currentTime
        }
        else -> { sunset - currentTime }
    }
}

