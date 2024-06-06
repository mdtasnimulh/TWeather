package com.tasnimulhasan.domain.repository.remote

import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow

interface HomeWeatherRepository {
    suspend fun fetchHomeWeatherData(params: HomeWeatherApiUseCase.Params): Flow<ApiResult<WeatherApiEntity>>

    suspend fun fetchWeatherOverview(params: WeatherDetailsApiUseCase.Params): Flow<ApiResult<WeatherDetailsApiEntity>>

    suspend fun fetchCityName(params: CitySearchApiUseCase.Params): Flow<ApiResult<List<CitySearchApiEntity>>>

    suspend fun fetchAirQualityIndex(params: AirQualityIndexApiUseCase.Params): Flow<ApiResult<List<AirQualityIndexApiEntity>>>
}