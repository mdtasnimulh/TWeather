package com.tasnimulhasan.common.constant

import com.tasnimulhasan.designsystem.R as Res

object AppConstants {
    const val DATA_PER_PAGE = 20
    var IS_DATA_SET_CHANGED = false

    const val SHORT_FORM_YES = "Y"
    const val SHORT_FORM_NO = "N"

    const val MAP_API_KEY = "AIzaSyBLOZSDmxFmKJxlhrfiDNsLBmj1T5qA9FY"
    const val LOCATION_STATUS_CODE = 1

    const val DEFAULT_LATITUDE = "23.777176"
    const val DEFAULT_LONGITUDE = "90.399452"

    val IMAGE_SELECTION_TYPE = arrayOf("image/png", "image/jpeg", "image/jpg")

    const val OPEN_WEATHER_API_KEY = "50f6d992404ddb49ea466d1da2bbef99"
    const val DATA_UNIT = "metric"
    const val BASE_URL = "https://api.openweathermap.org/"

    const val ONE_CALL_API_END_POINT = "data/3.0/onecall?"
    const val ONE_CALL_OVERVIEW_API_END_POINT = "data/3.0/onecall/overview?"
    const val CITY_SEARCH_END_POINT = "geo/1.0/direct?"

    val iconList = listOf(
        WeatherIcon(id = 1, iconId = "01d", iconRes = Res.drawable.clear_sky_day),
        WeatherIcon(id = 2, iconId = "01n", iconRes = Res.drawable.clear_sky_night),
        WeatherIcon(id = 3, iconId = "02d", iconRes = Res.drawable.few_clouds_d),
        WeatherIcon(id = 4, iconId = "02n", iconRes = Res.drawable.few_clouds_n),
        WeatherIcon(id = 5, iconId = "03d", iconRes = Res.drawable.scater_clouds),
        WeatherIcon(id = 6, iconId = "03n", iconRes = Res.drawable.scater_clouds),
        WeatherIcon(id = 7, iconId = "04d", iconRes = Res.drawable.broken_clouds),
        WeatherIcon(id = 8, iconId = "04n", iconRes = Res.drawable.broken_clouds),
        WeatherIcon(id = 9, iconId = "09d", iconRes = Res.drawable.rain_d),
        WeatherIcon(id = 10, iconId = "09n", iconRes = Res.drawable.rain_n),
        WeatherIcon(id = 11, iconId = "10d", iconRes = Res.drawable.rain_d),
        WeatherIcon(id = 12, iconId = "10n", iconRes = Res.drawable.rain_n),
        WeatherIcon(id = 13, iconId = "11d", iconRes = Res.drawable.thunder_d),
        WeatherIcon(id = 14, iconId = "11n", iconRes = Res.drawable.thunder_n),
        WeatherIcon(id = 15, iconId = "13d", iconRes = Res.drawable.snow),
        WeatherIcon(id = 16, iconId = "13n", iconRes = Res.drawable.snow),
        WeatherIcon(id = 17, iconId = "50d", iconRes = Res.drawable.mist_d),
        WeatherIcon(id = 18, iconId = "50n", iconRes = Res.drawable.mist_n),
    )

    data class WeatherIcon(
        val id: Int,
        val iconId: String,
        val iconRes: Int
    )
}