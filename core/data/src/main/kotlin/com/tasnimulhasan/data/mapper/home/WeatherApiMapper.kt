package com.tasnimulhasan.data.mapper.home

import com.tasnimulhasan.apiresponse.home.HomeWeatherApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.home.HomeWeatherApiEntity
import javax.inject.Inject

class WeatherApiMapper @Inject constructor() :
    Mapper<HomeWeatherApiResponse, HomeWeatherApiEntity> {
    override fun mapFromApiResponse(type: HomeWeatherApiResponse): HomeWeatherApiEntity {
        return HomeWeatherApiEntity(
            lat = (type.lat ?: "").toString(),
            lon = (type.lon ?: "").toString()
        )
    }
}
