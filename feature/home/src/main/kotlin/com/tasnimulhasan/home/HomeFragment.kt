package com.tasnimulhasan.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.entity.home.CurrentWeatherValue
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var errorHandler: ErrorUiHandler
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<HomeViewModel>()

    override fun viewBindingLayout(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        uiStateObserver()
        bindUiEvent()
        onClickListener()

        viewModel.action(UiAction.FetchWeatherData(getWeatherApiParams()))
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> this showLoader uiState.loading
                is UiState.ApiSuccess -> this showWeatherData uiState.weatherData
                is UiState.Error -> errorHandler.dataError(uiState.message){ /*NA*/ }
            }
        }
    }

    private infix fun showLoader(loading: Boolean) {
        if (loading) {
            showToastMessage("Loading, Please Wait!")
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity){
        binding.apply {
            setCurrentWeatherIcon(weatherData.currentWeather.currentWeatherValue)
            currentWeatherTv.text = getString(Res.string.format_current_weather, weatherData.currentWeather.currentTemp)
            currentWeatherConditionTv.text = weatherData.currentWeather.currentWeatherValue[0].currentWeatherCondition
        }
    }

    private fun setCurrentWeatherIcon(currentWeatherValue: List<CurrentWeatherValue>) {
        AppConstants.iconList.find { weatherValue ->
            weatherValue.iconId == currentWeatherValue[0].currentWeatherIcon
        }?.iconRes?.let { icon -> binding.currentWeatherIconIv.setImageResource(icon) }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private fun onClickListener(){
        binding.apply {

        }
    }

    private fun getWeatherApiParams(): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = AppConstants.DEFAULT_LATITUDE,
            lon = AppConstants.DEFAULT_LONGITUDE,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}