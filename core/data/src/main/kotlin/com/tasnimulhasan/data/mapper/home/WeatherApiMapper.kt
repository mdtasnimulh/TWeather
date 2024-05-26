package com.tasnimulhasan.data.mapper.home

import com.tasnimulhasan.apiresponse.home.WeatherApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.home.HomeWeatherApiEntity
import javax.inject.Inject

class WeatherApiMapper @Inject constructor() :
    Mapper<WeatherApiResponse, HomeWeatherApiEntity> {
    override fun mapFromApiResponse(type: WeatherApiResponse): HomeWeatherApiEntity {
        return HomeWeatherApiEntity(
            lat = (type.lat ?: "").toString(),
            lon = (type.lon ?: "").toString()
        )
    }
}
