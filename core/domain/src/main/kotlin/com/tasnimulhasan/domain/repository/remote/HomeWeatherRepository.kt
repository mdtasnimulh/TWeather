package com.tasnimulhasan.domain.repository.remote

import com.tasnimulhasan.domain.apiusecase.details.WeatherOverviewApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.entity.details.WeatherOverviewApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow

interface HomeWeatherRepository {
    suspend fun fetchHomeWeatherData(params: HomeWeatherApiUseCase.Params): Flow<ApiResult<WeatherApiEntity>>

    suspend fun fetchWeatherOverview(params: WeatherOverviewApiUseCase.Params): Flow<ApiResult<WeatherOverviewApiEntity>>
}