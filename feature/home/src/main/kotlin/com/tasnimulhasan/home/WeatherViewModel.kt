package com.tasnimulhasan.home

import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val airQualityIndexApiUseCase: AirQualityIndexApiUseCase,
    private val weatherDetailsApiUseCase: WeatherDetailsApiUseCase
) : BaseViewModel() {

    var isLocationGranted = MutableStateFlow(false)
    var latitude = ""
    var longitude = ""
    var cityName = ""
    var weatherData: WeatherApiEntity? = null
    var aqi = mutableListOf<AirQualityIndexApiEntity>()

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchWeatherData -> fetchWeatherData(getWeatherApiParams())
            is UiAction.FetchAirQualityIndex -> fetchAirQualityIndex(getAqiParams())
            is UiAction.FetchWeatherOverview -> fetchWeatherOverview(getOverviewApiParams())
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    private fun fetchWeatherData(params: HomeWeatherApiUseCase.Params) {
        execute {
            homeWeatherApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> {
                        weatherData = result.data
                        _uiState.value = UiState.ApiSuccess(result.data)
                    }
                }
            }
        }
    }

    private fun fetchAirQualityIndex(params: AirQualityIndexApiUseCase.Params) {
        execute {
            airQualityIndexApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> {
                        aqi.addAll(result.data)
                        _uiState.value = UiState.AirQualityIndex(result.data)
                    }
                }
            }
        }
    }

    private fun fetchWeatherOverview(params: WeatherDetailsApiUseCase.Params) {
        execute {
            weatherDetailsApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> _uiState.value = UiState.WeatherOverview(result.data)
                }
            }
        }
    }

    fun getWeatherApiParams(): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = latitude,
            lon = longitude,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }

    fun getAqiParams(): AirQualityIndexApiUseCase.Params {
        return AirQualityIndexApiUseCase.Params(
            lat = latitude,
            lon = longitude,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }

    fun getOverviewApiParams(): WeatherDetailsApiUseCase.Params {
        return WeatherDetailsApiUseCase.Params(
            lat = AppConstants.DEFAULT_LATITUDE,
            lon = AppConstants.DEFAULT_LONGITUDE,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}

sealed interface UiEvent {

}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class ApiSuccess(val weatherData: WeatherApiEntity) : UiState
    data class AirQualityIndex(val aqi: List<AirQualityIndexApiEntity>) : UiState
    data class WeatherOverview(val weatherOverview: WeatherDetailsApiEntity) : UiState
}

sealed interface UiAction {
    data class FetchWeatherData(val params: HomeWeatherApiUseCase.Params) : UiAction
    data class FetchAirQualityIndex(val params: AirQualityIndexApiUseCase.Params) : UiAction
    data class FetchWeatherOverview(val params: WeatherDetailsApiUseCase.Params) : UiAction
}