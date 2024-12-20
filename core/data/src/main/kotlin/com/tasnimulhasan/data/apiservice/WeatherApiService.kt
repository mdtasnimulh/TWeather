package com.tasnimulhasan.data.apiservice

import com.tasnimulhasan.apiresponse.aqi.AirQualityIndexApiResponse
import com.tasnimulhasan.apiresponse.city.SearchApiResponse
import com.tasnimulhasan.apiresponse.daily.DailyForecastApiResponse
import com.tasnimulhasan.apiresponse.details.WeatherDetailsApiResponse
import com.tasnimulhasan.apiresponse.home.WeatherApiResponse
import com.tasnimulhasan.common.constant.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET(AppConstants.ONE_CALL_API_END_POINT)
    suspend fun getHomeWeather(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") appid: String?,
        @Query("units") units: String?,
    ) : Response<WeatherApiResponse>

    @GET(AppConstants.ONE_CALL_OVERVIEW_API_END_POINT)
    suspend fun fetchWeatherOverView(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") appid: String?,
        @Query("units") units: String?,
    ) : Response<WeatherDetailsApiResponse>

    @GET(AppConstants.CITY_SEARCH_END_POINT)
    suspend fun fetchCityName(
        @Query("q") q: String?,
        @Query("limit") limit: Int?,
        @Query("appid") appid: String?,
    ) : Response<SearchApiResponse>

    @GET(AppConstants.AIR_POLLUTION_END_POINT)
    suspend fun fetchAirQualityIndex(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") appid: String?,
        @Query("units") units: String?,
    ) : Response<AirQualityIndexApiResponse>

    @GET(AppConstants.DAILY_FORECAST_END_POINT)
    suspend fun fetchDailyForecast (
        @Query("q") q: String?,
        @Query("cnt") cnt: Int?,
        @Query("appid") appid: String?,
        @Query("units") units: String?,
    ) : Response<DailyForecastApiResponse>

}