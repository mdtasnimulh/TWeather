package com.tasnimulhasan.city.citysearch

import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.domain.localusecase.city.InsertCityLocalUseCase
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val citySearchApiUseCase: CitySearchApiUseCase,
    private val insertCityLocalUseCase: InsertCityLocalUseCase,
) : BaseViewModel() {

    val action: (UiAction) -> Unit = { action ->
        when (action) {
            is UiAction.FetchCityName -> fetchCityName(action.params)
            is UiAction.InsertCities -> insertCitiesToLocalDb(action.city)
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    private fun fetchCityName(params: CitySearchApiUseCase.Params) {
        execute {
            citySearchApiUseCase.execute(params).collect { result ->
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

    private fun insertCitiesToLocalDb(city: CityListRoomEntity) {
        execute {
            val cityRoomEntity = CityListRoomEntity(
                id = city.id,
                name = city.name,
                cityName = city.cityName,
                lat = city.lat,
                lon = city.lon,
                country = city.country,
                state = city.state
            )
            insertCityLocalUseCase.invoke(InsertCityLocalUseCase.Params(cityRoomEntity))
            _uiEvent.send(UiEvent.NavigateToPreviousFragment)
        }
    }

}

sealed interface UiEvent {
    data object NavigateToPreviousFragment: UiEvent
}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class ApiSuccess(val cityEntity: List<CitySearchApiEntity>) : UiState
}

sealed interface UiAction {
    data class FetchCityName(val params: CitySearchApiUseCase.Params) : UiAction
    data class InsertCities(val city: CityListRoomEntity) : UiAction
}