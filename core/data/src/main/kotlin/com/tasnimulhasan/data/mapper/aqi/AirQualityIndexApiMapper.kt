package com.tasnimulhasan.data.mapper.aqi

import com.tasnimulhasan.apiresponse.aqi.AirQualityIndexApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.aqi.AqiDetails
import javax.inject.Inject

class AirQualityIndexApiMapper @Inject constructor() :
    Mapper<AirQualityIndexApiResponse, List<AirQualityIndexApiEntity>> {
    override fun mapFromApiResponse(type: AirQualityIndexApiResponse): List<AirQualityIndexApiEntity> {
        return type.list?.map { aqi ->
            AirQualityIndexApiEntity(
                aqi = aqi?.main?.aqi ?: 0,
                aqiDetails = AqiDetails(
                    carbonMonoxide = aqi?.components?.co ?: 0.0,
                    nitrogenOxide = aqi?.components?.no ?: 0.0,
                    nitrogenDioxide = aqi?.components?.no2 ?: 0.0,
                    ozone = aqi?.components?.o3 ?: 0.0,
                    sulfurDioxide = aqi?.components?.so2 ?: 0.0,
                    pm25 = aqi?.components?.pm2_5 ?: 0.0,
                    pm10 = aqi?.components?.pm10 ?: 0.0,
                    ammonia = aqi?.components?.nh3 ?: 0.0
                )
            )
        } ?: emptyList()
    }
}