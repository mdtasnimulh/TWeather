package com.tasnimulhasan.entity.aqi

data class AirQualityIndexApiEntity(
    val aqi: Int,
    val aqiDetails: AqiDetails
)

data class AqiDetails(
    val carbonMonoxide: Double,
    val nitrogenOxide: Double,
    val nitrogenDioxide: Double,
    val ozone: Double,
    val sulfurDioxide: Double,
    val pm25: Double,
    val pm10: Double,
    val ammonia: Double
)
