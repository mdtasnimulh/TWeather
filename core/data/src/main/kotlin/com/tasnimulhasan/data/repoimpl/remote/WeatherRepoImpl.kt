package com.tasnimulhasan.data.repoimpl.remote

import com.tasnimulhasan.data.NetworkBoundResource
import com.tasnimulhasan.data.apiservice.WeatherApiService
import com.tasnimulhasan.data.mapper.aqi.AirQualityIndexApiMapper
import com.tasnimulhasan.data.mapper.city.CitySearchApiMapper
import com.tasnimulhasan.data.mapper.daily.DailyForecastApiMapper
import com.tasnimulhasan.data.mapper.details.WeatherOverviewApiMapper
import com.tasnimulhasan.data.mapper.home.WeatherApiMapper
import com.tasnimulhasan.data.mapper.mapFromApiResponse
import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.domain.apiusecase.daily.DailyForecastApiUseCase
import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.entity.daily.DailyForecastApiEntity
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherApiMapper: WeatherApiMapper,
    private val weatherOverviewApiMapper: WeatherOverviewApiMapper,
    private val citySearchApiMapper: CitySearchApiMapper,
    private val airQualityIndexApiMapper: AirQualityIndexApiMapper,
    private val dailyForecastApiMapper: DailyForecastApiMapper,
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

    override suspend fun fetchWeatherOverview(params: WeatherDetailsApiUseCase.Params): Flow<ApiResult<WeatherDetailsApiEntity>> {
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

    override suspend fun fetchCityName(params: CitySearchApiUseCase.Params): Flow<ApiResult<List<CitySearchApiEntity>>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchCityName(
                    appid = params.appId,
                    q = params.query,
                    limit = params.limit
                )
            }, mapper = citySearchApiMapper
        )
    }

    override suspend fun fetchAirQualityIndex(params: AirQualityIndexApiUseCase.Params): Flow<ApiResult<List<AirQualityIndexApiEntity>>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchAirQualityIndex(
                    lat = params.lat,
                    lon = params.lon,
                    appid = params.appid,
                    units = params.units
                )
            }, mapper = airQualityIndexApiMapper
        )
    }

    override suspend fun fetchDailyForecast(params: DailyForecastApiUseCase.Params): Flow<ApiResult<DailyForecastApiEntity>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchDailyForecast(
                    q = params.name,
                    cnt = params.count,
                    appid = params.appId,
                    units = params.units
                )
            }, mapper = dailyForecastApiMapper
        )
    }
}