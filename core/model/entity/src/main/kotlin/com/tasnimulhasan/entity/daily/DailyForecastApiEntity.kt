package com.tasnimulhasan.entity.daily

data class DailyForecastApiEntity(
    val city: City,
    val cnt: Int,
    val cod: String,
    val dailyList: List<DailyForecast>
)

data class DailyForecast(
    val clouds: Int,
    val deg: Int,
    val dateTime: Long,
    val feelsLike: DailyFeelsLike,
    val gust: Double,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val speed: Double,
    val sunrise: Int,
    val sunset: Int,
    val temp: DailyTemp,
    val weather: List<DailyWeather>
)

data class City(
    val coordinate: DailyCoordinate,
    val country: String,
    val cityId: Int,
    val cityName: String,
    val population: Int,
    val timezone: Int
)

data class DailyCoordinate(
    val lat: Double,
    val lon: Double
)

data class DailyFeelsLike(
    val dayFeelsLike: Double,
    val eveFeelsLike: Double,
    val mornFeelsLike: Double,
    val nightFeelsLike: Double
)

data class DailyTemp(
    val dayTemp: Double,
    val eveTemp: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val mornTemp: Double,
    val nightTemp: Double
)

data class DailyWeather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)