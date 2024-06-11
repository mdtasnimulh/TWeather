package com.tasnimulhasan.city

import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.domain.localusecase.city.DeleteCityLocalUseCase
import com.tasnimulhasan.domain.localusecase.city.FetchCityLocalUseCase
import com.tasnimulhasan.domain.localusecase.city.InsertCityLocalUseCase
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val citySearchApiUseCase: CitySearchApiUseCase,
    private val insertCityLocalUseCase: InsertCityLocalUseCase,
    private val deleteCityLocalUseCase: DeleteCityLocalUseCase,
    private val fetchCityLocalUseCase: FetchCityLocalUseCase
) : BaseViewModel() {

    var cityList = mutableListOf<CityListRoomEntity>()

    val action: (UiAction) -> Unit = { action ->
        when (action) {
            is UiAction.FetchCityName -> fetchCityName(action.params)
            is UiAction.DeleteCities -> action.city.name?.let { deleteCities(action.city.id, it) }
            is UiAction.FetchCities -> fetchCities()
            is UiAction.InsertCities -> insertCitiesToLocalDb(action.city)
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        fetchCities()
    }

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

    private fun deleteCities(id: Int, name: String){
        execute {
            deleteCityLocalUseCase.invoke(DeleteCityLocalUseCase.Params(id = id, name = name))
        }
    }

    private fun fetchCities() {
        execute {
            fetchCityLocalUseCase.invoke().collect{
                cityList.clear()
                cityList.addAll(it)
                _uiState.value = UiState.CityList(it)
            }
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
    data class CityList(val cityList: List<CityListRoomEntity>) : UiState
}

sealed interface UiAction {
    data class FetchCityName(val params: CitySearchApiUseCase.Params) : UiAction
    data class InsertCities(val city: CityListRoomEntity) : UiAction
    data class DeleteCities(val city: CityListRoomEntity) : UiAction
    data object FetchCities : UiAction
}