package com.tasnimulhasan.domain.apiusecase.details

import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import javax.inject.Inject

class WeatherDetailsApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<WeatherDetailsApiUseCase.Params, WeatherDetailsApiEntity> {

    data class Params(
        val lat: String,
        val lon: String,
        val appid: String,
        val units: String
    )

    override suspend fun execute(params: Params) = repository.fetchWeatherOverview(params)
}