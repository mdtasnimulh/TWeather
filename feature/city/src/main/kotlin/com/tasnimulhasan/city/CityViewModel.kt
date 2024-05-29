package com.tasnimulhasan.city

import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val citySearchApiUseCase: CitySearchApiUseCase
) : BaseViewModel() {

    val action: (UiAction) -> Unit

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        action = {
            when (it) {
                is UiAction.FetchCityName -> fetchCityName(it.params)
            }
        }
    }

    private fun fetchCityName(params: CitySearchApiUseCase.Params) {
        execute {
             citySearchApiUseCase.execute(params).collect { result ->
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
    data class ApiSuccess(val cityEntity: List<CitySearchApiEntity>) : UiState
}

sealed interface UiAction {
    data class FetchCityName(val params: CitySearchApiUseCase.Params) : UiAction
}