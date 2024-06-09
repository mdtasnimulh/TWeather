package com.tasnimulhasan.home

import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val airQualityIndexApiUseCase: AirQualityIndexApiUseCase
) : BaseViewModel() {

    var isLocationGranted = MutableStateFlow<Boolean>(false)
    var latitude = ""
    var longitude = ""
    var cityName = ""

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchWeatherData -> fetchWeatherData(getWeatherApiParams())
            is UiAction.FetchAirQualityIndex -> fetchAirQualityIndex(getAqiParams())
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        /*fetchWeatherData(getWeatherApiParams())
        fetchAirQualityIndex(getAqiParams())*/
    }

    private fun fetchWeatherData(params: HomeWeatherApiUseCase.Params) {
        execute {
            homeWeatherApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> {
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
                    is ApiResult.Success -> _uiState.value = UiState.AirQualityIndex(result.data)
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
}

sealed interface UiEvent {

}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class ApiSuccess(val weatherData: WeatherApiEntity) : UiState
    data class AirQualityIndex(val aqi: List<AirQualityIndexApiEntity>) : UiState
}

sealed interface UiAction {
    data class FetchWeatherData(val params: HomeWeatherApiUseCase.Params) : UiAction
    data class FetchAirQualityIndex(val params: AirQualityIndexApiUseCase.Params) : UiAction
}