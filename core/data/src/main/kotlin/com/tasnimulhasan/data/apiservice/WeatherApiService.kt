package com.tasnimulhasan.data.apiservice

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

}