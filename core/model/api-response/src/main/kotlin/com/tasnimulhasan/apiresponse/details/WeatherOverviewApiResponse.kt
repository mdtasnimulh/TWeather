package com.tasnimulhasan.apiresponse.details

data class WeatherOverviewApiResponse(
    val date: String?,
    val lat: Double?,
    val lon: Double?,
    val tz: String?,
    val units: String?,
    val weather_overview: String?
)