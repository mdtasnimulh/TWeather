package com.tasnimulhasan.home

import android.os.Handler
import android.os.Looper
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.calculateRemainingTime
import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.details.RiseSet
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import com.tasnimulhasan.entity.home.WeatherApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val weatherDetailsApiUseCase: WeatherDetailsApiUseCase
) : BaseViewModel() {

    var latitude = ""
    var longitude = ""
    var cityName = ""
    var locality = ""
    var units: String = ""
    var exists = true
    private val handler = Handler(Looper.getMainLooper())

    private val _isFirst = MutableStateFlow(true)
    val isFirst: StateFlow<Boolean> = _isFirst

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchWeatherData -> fetchWeatherData(getWeatherApiParams())
            is UiAction.FetchWeatherOverview -> fetchWeatherOverview(getOverviewApiParams())
            is UiAction.RemainTime -> startRemainingTimer(it.sunrise, it.sunset)
            is UiAction.StopTimer -> stopTimer()
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private fun fetchWeatherData(params: HomeWeatherApiUseCase.Params) {
        execute {
            homeWeatherApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiEvent.send(UiEvent.Error(result.message))
                    is ApiResult.Loading -> _uiEvent.send(UiEvent.Loading(result.loading))
                    is ApiResult.Success -> {
                        _isFirst.value = false
                        _uiEvent.send(UiEvent.ApiSuccess(result.data))
                    }
                }
            }
        }
    }

    private fun fetchWeatherOverview(params: WeatherDetailsApiUseCase.Params) {
        execute {
            weatherDetailsApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Error -> _uiEvent.send(UiEvent.Error(result.message))
                    is ApiResult.Loading -> _uiEvent.send(UiEvent.Loading(result.loading))
                    is ApiResult.Success -> _uiEvent.send(UiEvent.WeatherOverview(result.data))
                }
            }
        }
    }

    fun getWeatherApiParams(): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = latitude,
            lon = longitude,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = units
        )
    }

    fun getOverviewApiParams(): WeatherDetailsApiUseCase.Params {
        return WeatherDetailsApiUseCase.Params(
            lat = AppConstants.DEFAULT_LATITUDE,
            lon = AppConstants.DEFAULT_LONGITUDE,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = units
        )
    }

    private fun startRemainingTimer(sunrise: Long, sunset: Long) {
        execute {
            handler.post(object : Runnable {
                override fun run() {
                    updateRemainingTime(sunrise, sunset)
                    handler.postDelayed(this, 1000)
                }
            })
        }
    }

    private fun updateRemainingTime(sunrise: Long, sunset: Long) {
        execute {
            val remainingTimeResult = calculateRemainingTime(sunrise, sunset)
            val remainingTimeInSeconds = remainingTimeResult.remainingTime

            if (remainingTimeInSeconds < 0) {
                _uiEvent.send(UiEvent.RemainingTimerValue("00:00:00"))
                return@execute
            }

            val hours = (remainingTimeInSeconds / 3600).toInt()
            val minutes = ((remainingTimeInSeconds % 3600) / 60).toInt()
            val seconds = (remainingTimeInSeconds % 60).toInt()
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

            val timeLabel = when (remainingTimeResult.timeType) {
                RiseSet.SUNRISE -> "$formattedTime\nTime to Sunrise"
                RiseSet.SUNSET -> "$formattedTime\nTime to Sunset"
                else -> "Time is up!"
            }
            _uiEvent.send(UiEvent.RemainingTimerValue(timeLabel))
        }
    }

    private fun stopTimer(){
        execute {
            handler.removeCallbacksAndMessages(null)
        }
    }
}

sealed interface UiEvent {
    data class Loading(val loading: Boolean) : UiEvent
    data class Error(val message: String) : UiEvent
    data class ApiSuccess(val weatherData: WeatherApiEntity) : UiEvent
    data class WeatherOverview(val weatherOverview: WeatherDetailsApiEntity) : UiEvent
    data class RemainingTimerValue(val time: String) : UiEvent
}

sealed interface UiAction {
    data class FetchWeatherData(val params: HomeWeatherApiUseCase.Params) : UiAction
    data class FetchWeatherOverview(val params: WeatherDetailsApiUseCase.Params) : UiAction
    data class RemainTime(val sunrise: Long, val sunset: Long) : UiAction
    data object StopTimer : UiAction
}