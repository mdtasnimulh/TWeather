package com.tasnimulhasan.city.citylist

import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.domain.localusecase.city.DeleteCityLocalUseCase
import com.tasnimulhasan.domain.localusecase.city.FetchCityLocalUseCase
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val deleteCityLocalUseCase: DeleteCityLocalUseCase,
    private val fetchCityLocalUseCase: FetchCityLocalUseCase,
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase
) : BaseViewModel() {

    var cityList = mutableListOf<CityListRoomEntity>()
    private val weatherDataList = mutableListOf<WeatherApiEntity>()

    val action: (UiAction) -> Unit = { action ->
        when (action) {
            is UiAction.DeleteCities -> action.city.name?.let { deleteCities(action.city.id, it) }
            is UiAction.FetchCities -> fetchCities()
            is UiAction.FetchWeather -> fetchWeatherForAllCities(action.params)
        }
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState get() = _uiState

    init {
        fetchCities()
    }

    private fun deleteCities(id: Int, name: String){
        execute {
            deleteCityLocalUseCase.invoke(DeleteCityLocalUseCase.Params(id = id, name = name))
        }
    }

    private fun fetchCities() {
        execute {
            fetchCityLocalUseCase.invoke().collect{
                //cityList.clear()
                cityList.addAll(it)
                _uiState.value = UiState.CityList(it)
                it.forEach { city ->
                    action(
                        UiAction.FetchWeather(
                            getWeatherParams(
                                city.lat.toString(),
                                city.lon.toString()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun fetchWeatherForAllCities(params: HomeWeatherApiUseCase.Params) {
        execute {
            homeWeatherApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = UiState.Loading(result.loading)
                    is ApiResult.Success -> {
                        weatherDataList.add(result.data)
                        Timber.e("chkWeatherDetails ${weatherDataList.size}")
                        _uiState.value = UiState.WeatherList(weatherDataList.toList())
                    }
                }
            }
        }
    }

    private fun getWeatherParams(lat: String, lon: String): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = lat,
            lon = lon,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class CityList(val cityList: List<CityListRoomEntity>) : UiState
    data class WeatherList(val weatherList: List<WeatherApiEntity>) : UiState
}

sealed interface UiAction {
    data class DeleteCities(val city: CityListRoomEntity) : UiAction
    data object FetchCities : UiAction
    data class FetchWeather(val params: HomeWeatherApiUseCase.Params) : UiAction
}