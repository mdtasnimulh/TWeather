package com.tasnimulhasan.apiresponse.home

data class WeatherApiResponse(
    val current: Current?,
    val daily: List<Daily?>?,
    val hourly: List<Hourly?>?,
    val lat: Double?,
    val lon: Double?,
    val minutely: List<Minutely?>?,
    val timezone: String?,
    val timezone_offset: Int?
)

data class Current(
    val clouds: Int?,
    val dew_point: Double?,
    val dt: Int?,
    val feels_like: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val rain: Rain?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<Weather?>?,
    val wind_deg: Int?,
    val wind_gust: Double?,
    val wind_speed: Double?
)

data class Daily(
    val clouds: Int?,
    val dew_point: Double?,
    val dt: Int?,
    val feels_like: FeelsLike?,
    val humidity: Int?,
    val moon_phase: Double?,
    val moonrise: Int?,
    val moonset: Int?,
    val pop: Double?,
    val pressure: Int?,
    val rain: Double?,
    val summary: String?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: Temp?,
    val uvi: Double?,
    val weather: List<Weather?>?,
    val wind_deg: Int?,
    val wind_gust: Double?,
    val wind_speed: Double?
)

data class FeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
)

data class Hourly(
    val clouds: Int?,
    val dew_point: Double?,
    val dt: Int?,
    val feels_like: Double?,
    val humidity: Int?,
    val pop: Double?,
    val pressure: Int?,
    val rain: Rain?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<Weather?>?,
    val wind_deg: Int?,
    val wind_gust: Double?,
    val wind_speed: Double?
)

data class Minutely(
    val dt: Int?,
    val precipitation: Double?
)

data class Rain(
    val `1h`: Double?
)

data class Temp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?
)

data class Weather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)