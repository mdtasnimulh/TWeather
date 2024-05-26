package com.tasnimulhasan.data.mapper.home

import com.tasnimulhasan.apiresponse.home.WeatherApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.home.CurrentWeather
import com.tasnimulhasan.entity.home.CurrentWeatherValue
import com.tasnimulhasan.entity.home.WeatherApiEntity
import javax.inject.Inject

class WeatherApiMapper @Inject constructor() :
    Mapper<WeatherApiResponse, WeatherApiEntity> {
    override fun mapFromApiResponse(type: WeatherApiResponse): WeatherApiEntity {
        return WeatherApiEntity(
            lat = type.lat ?: 0.0,
            lon = type.lon ?: 0.0,
            currentWeather = CurrentWeather(
                currentTemp = type.current?.temp ?: 0.0,
                currentClouds = type.current?.clouds ?: 0,
                currentFeelsLike = type.current?.feels_like ?: 0.0,
                currentPressure = type.current?.pressure ?: 0,
                currentHumidity = type.current?.humidity ?: 0,
                currentUvi = type.current?.uvi ?: 0.0,
                currentVisibility = type.current?.visibility ?: 0,
                currentWindSpeed = type.current?.wind_speed ?: 0.0,
                currentWeatherValue = type.current?.weather?.map {
                    CurrentWeatherValue(
                        currentWeatherIcon =  it.icon ?: "",
                        currentWeatherCondition = it.main ?: "",
                        currentWeatherConditionDesc = it.description ?: ""
                    )
                } ?: emptyList()
            )
        )
    }
}
