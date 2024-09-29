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