package com.tasnimulhasan.weatherdetails

import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val weatherDetailsApiUseCase: WeatherDetailsApiUseCase
) : BaseViewModel() {

    val action: (UiAction) -> Unit

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        action = {
            when (it) {
                is UiAction.FetchWeatherOverview -> fetchWeatherOverview(it.params)
            }
        }
    }

    private fun fetchWeatherOverview(params: WeatherDetailsApiUseCase.Params) {
        execute {
            weatherDetailsApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> _uiState.value = UiState.ApiSuccess(result.data)
                }
            }
        }
    }
}

sealed interface UiEvent {

}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class ApiSuccess(val weatherOverview: WeatherDetailsApiEntity) : UiState
}

sealed interface UiAction {
    data class FetchWeatherOverview(val params: WeatherDetailsApiUseCase.Params) : UiAction
}