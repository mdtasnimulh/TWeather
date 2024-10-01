package com.tasnimulhasan.common.extfun

import com.tasnimulhasan.entity.details.RiseSet
import com.tasnimulhasan.entity.details.SunriseSunsetRemainingTime

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

fun calculateRemainingTime(sunrise: Long, sunset: Long): SunriseSunsetRemainingTime {
    val currentTime = System.currentTimeMillis() / 1000
    return when {
        currentTime < sunrise -> {
            val remainingTime = sunrise - currentTime
            SunriseSunsetRemainingTime(remainingTime, RiseSet.SUNRISE)
        }
        currentTime > sunset -> {
            val nextDaySunrise = sunrise + 86400
            val remainingTime = nextDaySunrise - currentTime
            SunriseSunsetRemainingTime(remainingTime, RiseSet.SUNRISE)
        }
        else -> {
            val remainingTime = sunset - currentTime
            SunriseSunsetRemainingTime(remainingTime, RiseSet.SUNSET)
        }
    }
}


