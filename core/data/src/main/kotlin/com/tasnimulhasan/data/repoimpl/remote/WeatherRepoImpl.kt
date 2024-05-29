package com.tasnimulhasan.data.repoimpl.remote

import com.tasnimulhasan.data.NetworkBoundResource
import com.tasnimulhasan.data.apiservice.WeatherApiService
import com.tasnimulhasan.data.mapper.details.WeatherOverviewApiMapper
import com.tasnimulhasan.data.mapper.home.WeatherApiMapper
import com.tasnimulhasan.data.mapper.mapFromApiResponse
import com.tasnimulhasan.domain.apiusecase.details.WeatherOverviewApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.entity.details.WeatherOverviewApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherApiMapper: WeatherApiMapper,
    private val weatherOverviewApiMapper: WeatherOverviewApiMapper,
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

    override suspend fun fetchWeatherOverview(params: WeatherOverviewApiUseCase.Params): Flow<ApiResult<WeatherOverviewApiEntity>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchWeatherOverView(
                    lat = params.lat,
                    lon = params.lon,
                    appid = params.appid,
                    units = params.units
                )
            }, mapper = weatherOverviewApiMapper
        )
    }
}