package com.tasnimulhasan.data.mapper.details

import com.tasnimulhasan.apiresponse.details.WeatherDetailsApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import javax.inject.Inject

class WeatherOverviewApiMapper @Inject constructor() :
    Mapper<WeatherDetailsApiResponse, WeatherDetailsApiEntity> {
    override fun mapFromApiResponse(type: WeatherDetailsApiResponse): WeatherDetailsApiEntity {
        return WeatherDetailsApiEntity(
            lat = type.lat ?: 0.0,
            lon = type.lon ?: 0.0,
            timeZone = type.tz ?: "",
            date = type.date ?: "",
            weatherOverview = type.weather_overview ?: ""
        )
    }
}