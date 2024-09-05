package com.tasnimulhasan.domain.apiusecase.daily

import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.daily.DailyForecastApiEntity
import javax.inject.Inject

class DailyForecastApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<DailyForecastApiUseCase.Params, DailyForecastApiEntity> {

    data class Params(
        val name: String,
        val count: Int,
        val appId: String,
        val units: String
    )

    override suspend fun execute(params: Params) = repository.fetchDailyForecast(params)

}