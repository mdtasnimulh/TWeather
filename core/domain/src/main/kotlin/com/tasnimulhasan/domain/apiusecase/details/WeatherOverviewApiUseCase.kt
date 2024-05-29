package com.tasnimulhasan.domain.apiusecase.details

import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.details.WeatherOverviewApiEntity
import javax.inject.Inject

class WeatherOverviewApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<WeatherOverviewApiUseCase.Params, WeatherOverviewApiEntity> {

    data class Params(
        val lat: String,
        val lon: String,
        val appid: String,
        val units: String
    )

    override suspend fun execute(params: Params) = repository.fetchWeatherOverview(params)
}