package com.tasnimulhasan.domain.apiusecase.aqi

import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import javax.inject.Inject

class AirQualityIndexApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<AirQualityIndexApiUseCase.Params, List<AirQualityIndexApiEntity>> {

    data class Params(
        val lat: String,
        val lon: String,
        val appid: String,
        val units: String
    )

    override suspend fun execute(params: Params) = repository.fetchAirQualityIndex(params)

}