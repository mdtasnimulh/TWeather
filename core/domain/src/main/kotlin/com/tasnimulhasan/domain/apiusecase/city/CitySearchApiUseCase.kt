package com.tasnimulhasan.domain.apiusecase.city

import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.domain.usecase.ApiUseCaseParams
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import javax.inject.Inject

class CitySearchApiUseCase @Inject constructor(
    private val repository: HomeWeatherRepository
) : ApiUseCaseParams<CitySearchApiUseCase.Params, List<CitySearchApiEntity>> {

    data class Params(
        val appId: String,
        val query: String,
        val limit: Int
    )

    override suspend fun execute(params: Params) = repository.fetchCityName(params)
}