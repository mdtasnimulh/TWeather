package com.tasnimulhasan.data.mapper.city

import com.tasnimulhasan.apiresponse.city.SearchApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import javax.inject.Inject

class CitySearchApiMapper @Inject constructor() :
    Mapper<SearchApiResponse, List<CitySearchApiEntity>> {
    override fun mapFromApiResponse(type: SearchApiResponse): List<CitySearchApiEntity> {
        return type.map { result ->
            CitySearchApiEntity(
                name = result.name ?: "",
                cityName = result.local_names?.en ?: "",
                lat = result.lat ?: 0.0,
                lon = result.lon ?: 0.0,
                country = result.country ?: "",
                state = result.state ?: ""
            )
        }
    }
}