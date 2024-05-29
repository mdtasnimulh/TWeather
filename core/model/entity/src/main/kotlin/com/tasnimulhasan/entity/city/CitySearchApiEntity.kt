package com.tasnimulhasan.entity.city

data class CitySearchApiEntity(
    val name: String,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String
)
