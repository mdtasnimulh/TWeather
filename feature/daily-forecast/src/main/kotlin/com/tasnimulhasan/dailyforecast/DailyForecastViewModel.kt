package com.tasnimulhasan.dailyforecast

import com.tasnimulhasan.domain.apiusecase.daily.DailyForecastApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.daily.DailyForecastApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class DailyForecastViewModel @Inject constructor(
    private val dailyForecastApiUseCase: DailyForecastApiUseCase
) : BaseViewModel() {

    var units: String = ""

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchDailyForecast -> fetchDailyForecast(it.params)
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    private fun fetchDailyForecast(params: DailyForecastApiUseCase.Params) {
        execute {
            dailyForecastApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> _uiState.value = UiState.DailyForecast(result.data)
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
    data class DailyForecast(val dailyForecast: DailyForecastApiEntity) : UiState
}

sealed interface UiAction {
    data class FetchDailyForecast(val params: DailyForecastApiUseCase.Params) : UiAction
}