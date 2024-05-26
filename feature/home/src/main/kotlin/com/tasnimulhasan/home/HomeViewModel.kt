package com.tasnimulhasan.home

import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.home.HomeWeatherApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase
) : BaseViewModel() {

    val action: (UiAction) -> Unit

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        action = {
            when (it) {
                is UiAction.FetchWeatherData -> fetchWeatherData(it.params)
            }
        }
    }

    private fun fetchWeatherData(params: HomeWeatherApiUseCase.Params) {
        execute {
            homeWeatherApiUseCase.execute(params).collect{ result ->
                when (result) {
                    is ApiResult.Error -> {
                        _uiEvent.send(UiEvent.ShowError(result.message))
                        Timber.e("chkWeatherData ${result.message}${result.code}")
                    }
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> {
                        Timber.e("chkWeatherData ${result.data}")
                        _uiState.value = UiState.ApiSuccess(result.data)
                    }
                }
            }
        }
    }

}

sealed interface UiEvent {
    data class ShowError(val message: String) : UiEvent
}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class ApiSuccess(val weatherData: HomeWeatherApiEntity) : UiState
}

sealed interface UiAction {
    data class FetchWeatherData(val params: HomeWeatherApiUseCase.Params) : UiAction
}