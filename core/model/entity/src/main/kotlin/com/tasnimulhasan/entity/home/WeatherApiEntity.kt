package com.tasnimulhasan.entity.home

data class WeatherApiEntity(
    val lat: Double,
    val lon: Double,
    val currentWeather: CurrentWeather,
)

data class CurrentWeather(
    val currentTemp: Double,
    val currentClouds: Int,
    val currentFeelsLike: Double,
    val currentPressure: Int,
    val currentHumidity: Int,
    val currentUvi: Double,
    val currentVisibility: Int,
    val currentWindSpeed: Double,
    val currentWeatherValue: List<CurrentWeatherValue>
)

data class CurrentWeatherValue(
    val currentWeatherIcon: String,
    val currentWeatherCondition: String,
    val currentWeatherConditionDesc: String
)