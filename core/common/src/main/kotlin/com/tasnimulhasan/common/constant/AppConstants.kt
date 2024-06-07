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
    const val AIR_POLLUTION_END_POINT = "data/2.5/air_pollution/forecast?"

    //--- Weather Condition Names ---//
    private const val WEATHER_CONDITION_CLEAR_SKY = "Clear Sky"
    private const val WEATHER_CONDITION_FEW_CLOUDS = "Few Clouds"
    private const val WEATHER_CONDITION_SCATTERED_CLOUDS = "Scattered Clouds"
    private const val WEATHER_CONDITION_BROKEN_CLOUDS = "Broken Clouds"
    private const val WEATHER_CONDITION_SHOWER_RAIN = "Shower Rain"
    private const val WEATHER_CONDITION_RAIN = "Rain"
    private const val WEATHER_CONDITION_THUNDERSTORM = "Thunderstorm"
    private const val WEATHER_CONDITION_SNOW = "Snow"
    private const val WEATHER_CONDITION_MIST = "Mist"
    //--- Weather Condition Names ---//

    //--- Weather Icon Sets ---//
    val iconSetTwo = listOf(
        WeatherIcon(id = 1, iconId = "01d", iconRes = Res.drawable.ic_clear_sky_day, condition = WEATHER_CONDITION_CLEAR_SKY),
        WeatherIcon(id = 2, iconId = "01n", iconRes = Res.drawable.ic_clear_sky_night, condition = WEATHER_CONDITION_CLEAR_SKY),
        WeatherIcon(id = 3, iconId = "02d", iconRes = Res.drawable.ic_cloudy_day, condition = WEATHER_CONDITION_FEW_CLOUDS),
        WeatherIcon(id = 4, iconId = "02n", iconRes = Res.drawable.ic_cloud_night, condition = WEATHER_CONDITION_FEW_CLOUDS),
        WeatherIcon(id = 5, iconId = "03d", iconRes = Res.drawable.ic_overcast_cloud, condition = WEATHER_CONDITION_SCATTERED_CLOUDS),
        WeatherIcon(id = 6, iconId = "03n", iconRes = Res.drawable.ic_overcast_cloud, condition = WEATHER_CONDITION_SCATTERED_CLOUDS),
        WeatherIcon(id = 7, iconId = "04d", iconRes = Res.drawable.ic_sunny_day, condition = WEATHER_CONDITION_BROKEN_CLOUDS),
        WeatherIcon(id = 8, iconId = "04n", iconRes = Res.drawable.ic_night, condition = WEATHER_CONDITION_BROKEN_CLOUDS),
        WeatherIcon(id = 9, iconId = "09d", iconRes = Res.drawable.ic_stormy_weather, condition = WEATHER_CONDITION_SHOWER_RAIN),
        WeatherIcon(id = 10, iconId = "09n", iconRes = Res.drawable.ic_stormy_weather, condition = WEATHER_CONDITION_SHOWER_RAIN),
        WeatherIcon(id = 11, iconId = "10d", iconRes = Res.drawable.ic_rain, condition = WEATHER_CONDITION_RAIN),
        WeatherIcon(id = 12, iconId = "10n", iconRes = Res.drawable.ic_rain, condition = WEATHER_CONDITION_RAIN),
        WeatherIcon(id = 13, iconId = "11d", iconRes = Res.drawable.ic_thunderstorm, condition = WEATHER_CONDITION_THUNDERSTORM),
        WeatherIcon(id = 14, iconId = "11n", iconRes = Res.drawable.ic_thunderstorm, condition = WEATHER_CONDITION_THUNDERSTORM),
        WeatherIcon(id = 15, iconId = "13d", iconRes = Res.drawable.ic_snow_fall, condition = WEATHER_CONDITION_SNOW),
        WeatherIcon(id = 16, iconId = "13n", iconRes = Res.drawable.ic_snow_fall, condition = WEATHER_CONDITION_SNOW),
        WeatherIcon(id = 17, iconId = "50d", iconRes = Res.drawable.ic_fogg, condition = WEATHER_CONDITION_MIST),
        WeatherIcon(id = 18, iconId = "50n", iconRes = Res.drawable.ic_fogg, condition = WEATHER_CONDITION_MIST),
    )
    //--- Weather Icon Sets ---//

    val aqiValues = listOf(
        AqiValueToReadableValue(aqi = 1, name = "Good"),
        AqiValueToReadableValue(aqi = 2, name = "Fair"),
        AqiValueToReadableValue(aqi = 3, name = "Moderate"),
        AqiValueToReadableValue(aqi = 4, name = "Poor"),
        AqiValueToReadableValue(aqi = 5, name = "Very Poor"),
    )
    val aqiValuesUs = listOf(
        AqiValueToReadableValueUs(aqi = 1, name = "Good", lowPm = 0.00, highPm = 50.00),
        AqiValueToReadableValueUs(aqi = 2, name = "Moderate", lowPm = 50.01, highPm = 100.00),
        AqiValueToReadableValueUs(aqi = 3, name = "Unhealthy", lowPm = 100.01, highPm = 200.00),
        AqiValueToReadableValueUs(aqi = 4, name = "Very unhealthy", lowPm = 200.01, highPm = 300.00),
        AqiValueToReadableValueUs(aqi = 5, name = "Hazardous", lowPm = 300.01, highPm = 500.00),
        AqiValueToReadableValueUs(aqi = 6, name = "Very Hazardous", lowPm = 500.01, highPm = 1000.00),
    )

    data class WeatherIcon(
        val id: Int,
        val iconId: String,
        val iconRes: Int,
        val condition: String
    )

    data class AqiValueToReadableValue(
        val aqi: Int,
        val name: String
    )
    data class AqiValueToReadableValueUs(
        val aqi: Int,
        val name: String,
        val lowPm: Double,
        val highPm: Double
    )
}