package com.tasnimulhasan.entity.details

data class SunriseSunsetRemainingTime(
    val remainingTime: Long,
    val timeType: RiseSet
)

enum class RiseSet {
    SUNRISE,
    SUNSET
}