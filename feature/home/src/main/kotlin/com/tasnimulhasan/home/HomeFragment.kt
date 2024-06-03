package com.tasnimulhasan.home

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.navigateToDestination
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

    override fun viewBindingLayout(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        uiStateObserver()
        bindUiEvent()
        onClickListener()
        setDetailsTvTextColor()

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

    private fun setTextColor(textView: AppCompatTextView, palette: Palette) {
        textView.paint.shader = LinearGradient(
            0f, 0f, textView.paint.measureText(textView.text.toString()), textView.textSize,
            palette.vibrantSwatch?.rgb!!, (palette.vibrantSwatch?.rgb!! and 0x66FFFFFF),
            Shader.TileMode.CLAMP
        )
    }

    private fun setDetailsTvTextColor() {
        binding.currentWeatherDetailsIncl.apply {
            realFeelTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            humidityTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            pressureTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            uvIndexTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            windTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            rainTv.setTextColor(setDetailsTvTextColor(Res.drawable.ic_uvi))
            realFeelValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
            humidityValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
            pressureValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
            uvIndexValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
            windValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
            rainValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.ic_uvi))
        }
    }

    private fun setDetailsValueTextColor(image: Int): Int{
        return Palette.from(
            ContextCompat.getDrawable(requireContext(), image)?.toBitmap()!!
        ).generate().vibrantSwatch?.bodyTextColor ?: ContextCompat.getColor(requireContext(), Res.color.white)
    }

    private fun setDetailsTvTextColor(image: Int): Int{
        return Palette.from(
            ContextCompat.getDrawable(requireContext(), image)?.toBitmap()!!
        ).generate().vibrantSwatch?.titleTextColor ?: ContextCompat.getColor(requireContext(), Res.color.white)
    }

    override fun isEnableEdgeToEdge() = true
}