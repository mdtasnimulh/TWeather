package com.tasnimulhasan.entity.home

data class WeatherApiEntity(
    val lat: Double,
    val lon: Double,
    val currentWeatherData: CurrentWeatherData,
    val dailyWeatherData: List<DailyWeatherData>,
    val hourlyWeatherData: List<HourlyWeatherData>
)

// Current Weather Data //
data class CurrentWeatherData(
    val currentSunrise: Long,
    val currentSunset: Long,
    val currentTemp: Double,
    val currentFeelsLike: Double,
    val currentPressure: Int,
    val currentHumidity: Int,
    val currentDewPoint: Double,
    val currentUvi: Double,
    val currentClouds: Int,
    val currentVisibility: Int,
    val currentWindSpeed: Double,
    val currentWindDegree: Int,
    val currentWindGust: Double,
    val currentRain: Double,
    val currentWeatherCondition: List<CurrentWeatherConditionData>
)

data class CurrentWeatherConditionData(
    val currentWeatherIcon: String,
    val currentWeatherCondition: String,
    val currentWeatherConditionDesc: String
)

// Daily Weather Data //
data class DailyWeatherData(
    val dailySunrise: Long,
    val dailySunSet: Long,
    val dailyMoonRise: Long,
    val dailyMoonSet: Long,
    val dailyMoonPhase: Double,
    val dailySummary: String,
    val dailyTemp: DailyTemperatureData,
    val dailyFeelsLike: DailyFeelsLikeData,
    val dailyPressure: Int,
    val dailyHumidity: Int,
    val dailyDewPoint: Double,
    val dailyWindSpeed: Double,
    val dailyWindDegree: Int,
    val dailyWindGust: Double,
    val dailyWeatherCondition: List<DailyWeatherConditionData>,
    val dailyRain: Double,
    val dailyClouds: Int,
    val dailyUvi: Double
)

data class DailyFeelsLikeData(
    val dailyDayFeelsLike: Double,
    val dailyNightFeelsLike: Double,
    val dailyMorningFeelsLike: Double,
    val dailyEveningFeelsLike: Double
)

data class DailyTemperatureData(
    val dailyDayTemperature: Double,
    val dailyNightTemperature: Double,
    val dailyMorningTemperature: Double,
    val dailyEveningTemperature: Double,
    val dailyMinimumTemperature: Double,
    val dailyMaximumTemperature: Double,
)

data class DailyWeatherConditionData(
    val dailyWeatherIcon: String,
    val dailyWeatherCondition: String,
    val dailyWeatherConditionDesc: String
)

// Hourly Weather Data //
data class HourlyWeatherData(
    val hourlyTemperature: Double,
    val hourlyFeelsLike: Double,
    val hourlyPressure: Int,
    val hourlyHumidity: Int,
    val hourlyDewPoint: Double,
    val hourlyUvi: Double,
    val hourlyClouds: Int,
    val hourlyVisibility: Int,
    val hourlyWindSpeed: Double,
    val hourlyWindDegree: Int,
    val hourlyWindGust: Double,
    val hourlyRain: Double,
    val hourlyWeatherCondition: List<HourlyWeatherConditionData>,
)

data class HourlyWeatherConditionData(
    val hourlyWeatherIcon: String,
    val hourlyWeatherCondition: String,
    val hourlyWeatherConditionDesc: String
)