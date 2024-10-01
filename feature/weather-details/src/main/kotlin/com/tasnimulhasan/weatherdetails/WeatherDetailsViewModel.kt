package com.tasnimulhasan.weatherdetails

import android.os.Handler
import android.os.Looper
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.extfun.calculateRemainingTime
import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.domain.base.BaseViewModel
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.details.RiseSet
import com.tasnimulhasan.entity.home.WeatherApiEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val airQualityIndexApiUseCase: AirQualityIndexApiUseCase,
) : BaseViewModel() {

    var aqi = mutableListOf<AirQualityIndexApiEntity>()
    var units: String = ""
    var exists = true
    private val handler = Handler(Looper.getMainLooper())

    val action: (UiAction) -> Unit = {
        when (it) {
            is UiAction.FetchWeatherData -> fetchWeatherData(it.params)
            is UiAction.FetchAirQualityIndex -> fetchAirQualityIndex(it.params)
            is UiAction.StartTimer -> startTimer()
            is UiAction.StopTimer -> stopTimer()
            is UiAction.RemainTime -> startRemainingTimer(it.sunrise, it.sunset)
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

    private fun startTimer() {
        execute {
            handler.post(object : Runnable {
                override fun run() {
                    updateTime()
                    handler.postDelayed(this, 1000)
                }
            })
        }
    }

    private fun updateTime() {
        execute {
            val currentTime = Calendar.getInstance().time
            val sdf = SimpleDateFormat(DateTimeFormat.outputHMA, Locale.getDefault())
            _uiState.value = UiState.TimerValue(sdf.format(currentTime))
        }
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
                _uiState.value = UiState.RemainingTimerValue("00:00:00")
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
            _uiState.value = UiState.RemainingTimerValue(timeLabel)
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
}

sealed interface UiState {
    data class Loading(val loading: Boolean) : UiState
    data class Error(val message: String) : UiState
    data class ApiSuccess(val weatherData: WeatherApiEntity) : UiState
    data class AirQualityIndex(val aqi: List<AirQualityIndexApiEntity>) : UiState
    data class TimerValue(val time: String) : UiState
    data class RemainingTimerValue(val time: String) : UiState
}

sealed interface UiAction {
    data class FetchWeatherData(val params: HomeWeatherApiUseCase.Params) : UiAction
    data class FetchAirQualityIndex(val params: AirQualityIndexApiUseCase.Params) : UiAction
    data object StartTimer : UiAction
    data object StopTimer : UiAction
    data class RemainTime(val sunrise: Long, val sunset: Long) : UiAction
}