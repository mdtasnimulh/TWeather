package com.tasnimulhasan.domain.repository.remote

import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow

interface HomeWeatherRepository {
    suspend fun fetchHomeWeatherData(params: HomeWeatherApiUseCase.Params): Flow<ApiResult<WeatherApiEntity>>
}