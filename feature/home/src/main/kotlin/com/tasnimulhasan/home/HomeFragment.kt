package com.tasnimulhasan.home

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.loadGifImage
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setDetailsTvTextColor
import com.tasnimulhasan.common.extfun.setDetailsValueTextColor
import com.tasnimulhasan.common.extfun.setTextColor
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.entity.home.CurrentWeatherConditionData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<HomeViewModel>()

    override fun viewBindingLayout(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        uiStateObserver()
        bindUiEvent()
        onClickListener()
        setDetailsTextColor()
        setImage()

        viewModel.action(UiAction.FetchWeatherData(getWeatherApiParams()))
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> this showLoader uiState.loading
                is UiState.ApiSuccess -> this showWeatherData uiState.weatherData
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
            }
        }
    }

    private infix fun showLoader(loading: Boolean) {
        if (loading) {
            showToastMessage("Loading, Please Wait!")
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity) {
        binding.apply {
            setCurrentWeatherIcon(weatherData.currentWeatherData.currentWeatherCondition)
            currentWeatherTv.text = getString(Res.string.format_current_weather, weatherData.currentWeatherData.currentTemp)
            currentWeatherConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition

            currentWeatherDetailsIncl.apply {
                maxValueTv.text = getString(Res.string.format_current_weather, weatherData.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature)
                minValueTv.text = getString(Res.string.format_current_weather, weatherData.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature)
                visibilityValueTv.text = getString(Res.string.format_visibility, weatherData.currentWeatherData.currentVisibility/1000)
                realFeelValueTv.text = getString(Res.string.format_current_weather, weatherData.currentWeatherData.currentFeelsLike)
                humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
                pressureValueTv.text = getString(Res.string.format_air_pressure, weatherData.currentWeatherData.currentPressure.toString())
                windValueTv.text = getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
                uvIndexValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)
                rainValueTv.text = getString(Res.string.format_rain, weatherData.currentWeatherData.currentRain)
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private fun onClickListener() {
        binding.apply {
            currentWeatherIconIv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_weather_details_fragment).toUri())
            }

            currentWeatherTv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_city_search_fragment).toUri())
            }
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

    private fun setCurrentWeatherIcon(currentWeatherConditionData: List<CurrentWeatherConditionData>) {
        AppConstants.iconSetTwo.find { weatherValue ->
            weatherValue.iconId == currentWeatherConditionData[0].currentWeatherIcon
        }?.iconRes?.let { icon ->
            binding.currentWeatherIconIv.setImageResource(icon)
            setTextColor(binding.currentWeatherTv, Palette.from(ContextCompat.getDrawable(requireContext(), icon)?.toBitmap()!!).generate())
        }
    }

    private fun setDetailsTextColor() {
        binding.currentWeatherDetailsIncl.apply {
            // text color for details title //
            maxTv.setTextColor(setDetailsTvTextColor(Res.drawable.max_temp, requireContext()))
            minTv.setTextColor(setDetailsTvTextColor(Res.drawable.max_temp, requireContext()))
            visibilityTv.setTextColor(setDetailsTvTextColor(Res.drawable.visibility, requireContext()))
            realFeelTv.setTextColor(setDetailsTvTextColor(Res.drawable.real_feel, requireContext()))
            humidityTv.setTextColor(setDetailsTvTextColor(Res.drawable.current_humidity, requireContext()))
            pressureTv.setTextColor(setDetailsTvTextColor(Res.drawable.air_pressure, requireContext()))
            uvIndexTv.setTextColor(setDetailsTvTextColor(Res.drawable.uvi, requireContext()))
            windTv.setTextColor(setDetailsTvTextColor(Res.drawable.wind, requireContext()))
            rainTv.setTextColor(setDetailsTvTextColor(Res.drawable.rain, requireContext()))
            // text color for details value //
            maxValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.max_temp, requireContext()))
            minValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.max_temp, requireContext()))
            visibilityValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.visibility, requireContext()))
            realFeelValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.real_feel, requireContext()))
            humidityValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.current_humidity, requireContext()))
            pressureValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.air_pressure, requireContext()))
            uvIndexValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.uvi, requireContext()))
            windValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.wind, requireContext()))
            rainValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.rain, requireContext()))
        }
    }

    private fun setImage() {
        binding.currentWeatherDetailsIncl.maxIv.loadGifImage(Res.drawable.max_temp, requireContext())
        binding.currentWeatherDetailsIncl.minIv.loadGifImage(Res.drawable.max_temp, requireContext())
        binding.currentWeatherDetailsIncl.visibilityIv.loadGifImage(Res.drawable.visibility, requireContext())
        binding.currentWeatherDetailsIncl.realFeelIv.loadGifImage(Res.drawable.real_feel, requireContext())
        binding.currentWeatherDetailsIncl.humidityIv.loadGifImage(Res.drawable.current_humidity, requireContext())
        binding.currentWeatherDetailsIncl.pressureIv.loadGifImage(Res.drawable.air_pressure, requireContext())
        binding.currentWeatherDetailsIncl.uvIndexIv.loadGifImage(Res.drawable.uvi, requireContext())
        binding.currentWeatherDetailsIncl.windIv.loadGifImage(Res.drawable.wind, requireContext())
        binding.currentWeatherDetailsIncl.rainIv.loadGifImage(Res.drawable.rain, requireContext())
    }

    override fun isEnableEdgeToEdge() = true
}