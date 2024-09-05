package com.tasnimulhasan.apiresponse.daily

data class DailyForecastApiResponse(
    val city: City?,
    val cnt: Int?,
    val cod: String?,
    val list: List<DailyForecast?>?,
    val message: Double?
)

data class DailyForecast(
    val clouds: Int?,
    val deg: Int?,
    val dt: Int?,
    val feels_like: DailyFeelsLike?,
    val gust: Double?,
    val humidity: Int?,
    val pop: Double?,
    val pressure: Int?,
    val rain: Double?,
    val speed: Double?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: DailyTemp?,
    val weather: List<DailyWeather?>?
)

data class City(
    val coord: DailyCoordinate?,
    val country: String?,
    val id: Int?,
    val name: String?,
    val population: Int?,
    val timezone: Int?
)

data class DailyCoordinate(
    val lat: Double?,
    val lon: Double?
)

data class DailyFeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
)

data class DailyTemp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?
)

data class DailyWeather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)