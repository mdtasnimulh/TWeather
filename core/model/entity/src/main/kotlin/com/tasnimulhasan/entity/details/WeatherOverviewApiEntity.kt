package com.tasnimulhasan.entity.details

data class WeatherOverviewApiEntity(
    val lat: Double,
    val lon: Double,
    val timeZone: String,
    val date: String,
    val weatherOverview: String
)
