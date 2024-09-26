package com.tasnimulhasan.weatherdetails

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
class WeatherDetailsViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val airQualityIndexApiUseCase: AirQualityIndexApiUseCase,
) : BaseViewModel() {

    var aqi = mutableListOf<AirQualityIndexApiEntity>()
    var units: String = ""

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchWeatherData -> fetchWeatherData(it.params)
            is UiAction.FetchAirQualityIndex -> fetchAirQualityIndex(it.params)
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
                    is ApiResult.Loading -> _uiEvent.send(UiEvent.Loading(result.loading))
                    is ApiResult.Success -> _uiState.value = UiState.ApiSuccess(result.data)
                }
            }
        }
    }

    private fun fetchAirQualityIndex(params: AirQualityIndexApiUseCase.Params) {
        execute {
            airQualityIndexApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiEvent.send(UiEvent.Loading(result.loading))
                    is ApiResult.Success -> {
                        _uiState.value = UiState.AirQualityIndex(result.data)
                        aqi.addAll(result.data)
                    }
                }
            }
        }
    }
}

sealed interface UiEvent {
    data class Loading(val loading: Boolean) : UiEvent
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