package com.tasnimulhasan.weatherdetails

import com.tasnimulhasan.domain.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(

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

}