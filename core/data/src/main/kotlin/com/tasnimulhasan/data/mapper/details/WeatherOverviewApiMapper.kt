package com.tasnimulhasan.data.mapper.details

import com.tasnimulhasan.apiresponse.details.WeatherOverviewApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.details.WeatherOverviewApiEntity
import javax.inject.Inject

class WeatherOverviewApiMapper @Inject constructor() :
    Mapper<WeatherOverviewApiResponse, WeatherOverviewApiEntity> {
    override fun mapFromApiResponse(type: WeatherOverviewApiResponse): WeatherOverviewApiEntity {
        return WeatherOverviewApiEntity(
            lat = type.lat ?: 0.0,
            lon = type.lon ?: 0.0,
            timeZone = type.tz ?: "",
            date = type.date ?: "",
            weatherOverview = type.weather_overview ?: ""
        )
    }
}