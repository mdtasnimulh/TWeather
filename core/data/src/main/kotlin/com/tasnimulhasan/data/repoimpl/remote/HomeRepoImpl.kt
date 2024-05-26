package com.tasnimulhasan.data.repoimpl.remote

import com.tasnimulhasan.data.NetworkBoundResource
import com.tasnimulhasan.data.apiservice.WeatherApiService
import com.tasnimulhasan.data.mapper.home.WeatherApiMapper
import com.tasnimulhasan.data.mapper.mapFromApiResponse
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherApiMapper: WeatherApiMapper,
    private val networkBoundResources: NetworkBoundResource
) : HomeWeatherRepository {
    override suspend fun fetchHomeWeatherData(params: HomeWeatherApiUseCase.Params): Flow<ApiResult<WeatherApiEntity>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.getHomeWeather(
                    lat = params.lat,
                    lon = params.lon,
                    appid = params.appid,
                    units = params.units
                )
            }, mapper = weatherApiMapper
        )
    }
}