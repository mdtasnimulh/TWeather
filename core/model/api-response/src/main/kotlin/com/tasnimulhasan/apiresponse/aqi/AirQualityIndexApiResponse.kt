package com.tasnimulhasan.apiresponse.aqi

data class AirQualityIndexApiResponse(
    val coord: Cord?,
    val list: List<Components?>?
)

data class Cord(
    val lon: Double?,
    val lat: Double?
)

data class Components(
    val main: Main?,
    val components: ComponentsValue?,
    val dt: Int?
)
data class Main(
    val aqi: Int?
)

data class ComponentsValue(
    val co: Double?,
    val no: Double?,
    val no2: Double?,
    val o3: Double?,
    val so2: Double?,
    val pm2_5: Double?,
    val pm10: Double?,
    val nh3: Double?
)