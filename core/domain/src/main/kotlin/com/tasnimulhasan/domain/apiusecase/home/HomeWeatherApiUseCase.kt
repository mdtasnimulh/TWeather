package com.tasnimulhasan.domain.apiusecase.home

import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.home.HomeWeatherApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeWeatherApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<HomeWeatherApiUseCase.Params, HomeWeatherApiEntity> {

    data class Params(
        val lat: String,
        val lon: String,
        val appid: String,
        val units: String
    )

    override suspend fun execute(params: Params) = repository.fetchHomeWeatherData(params)

}
