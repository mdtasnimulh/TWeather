package com.tasnimulhasan.weatherdetails

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.calculateProgressBySunriseSunset
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.getColorForAqiName
import com.tasnimulhasan.common.extfun.setUpHorizontalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.domain.apiusecase.aqi.AirQualityIndexApiUseCase
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.home.HourlyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.weatherdetails.databinding.FragmentWeatherDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class WeatherDetailsFragment : BaseFragment<FragmentWeatherDetailsBinding>() {

    @Inject lateinit var gson: Gson
    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var errorHandler: ErrorUiHandler
    private val viewModel by viewModels<WeatherDetailsViewModel>()
    private var hourlyAdapter by autoCleared<HourlyAdapter>()
    private val args by navArgs<WeatherDetailsFragmentArgs>()

    override fun viewBindingLayout() = FragmentWeatherDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureNsv)

        viewModel.exists =
            if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) true
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) false
            else true

        setUnit()
        initToolbar()
        uiStateObserver()
        bindUiEvent()
        onClickListener()

        viewModel.action(UiAction.FetchWeatherData(getWeatherApiParams()))
        viewModel.action(UiAction.FetchAirQualityIndex(getAqiParams()))
    }

    private fun setUnit() {
        if (viewModel.exists) viewModel.units = AppConstants.DATA_UNIT_CELSIUS
        else viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_weather_overview)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private infix fun initHourlyRecyclerView(hourlyWeatherData: List<HourlyWeatherData>) {
        hourlyAdapter = HourlyAdapter(viewModel.exists) {

        }
        requireContext().setUpHorizontalRecyclerView(binding.hourlyRv, hourlyAdapter)
        hourlyAdapter.submitList(hourlyWeatherData)
        hourlyAdapter.notifyItemRangeChanged(0, hourlyAdapter.itemCount)
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading ->{}
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
                is UiState.ApiSuccess -> {
                    this showData uiState.weatherData
                    this initHourlyRecyclerView uiState.weatherData.hourlyWeatherData.take(24)
                }
                is UiState.AirQualityIndex -> showAirQuality(uiState.aqi[0])
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { event ->
            when (event) {
                is UiEvent.Loading -> errorHandler.showProgressBar(event.loading)
            }
        }
    }

    private infix fun showData(weatherData: WeatherApiEntity) {
        binding.apply {
            todayTempTv.text = resources.getString(if (viewModel.exists) Res.string.format_temperature else Res.string.format_temperature_f, weatherData.currentWeatherData.currentTemp)
            todayConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
            cityDayTv.text = resources.getString(Res.string.format_city_day, args.cityName, convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.DAY_TIME_FORMAT))

            AppConstants.iconSetTwo.find { weatherValue ->
                weatherValue.iconId == weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon
            }?.iconRes?.let { icon ->
                binding.weatherConditionIv.setImageResource(icon)
            }

            currentWeatherDetailsIncl.apply {
                maxValueTv.text = resources.getString(if (viewModel.exists) Res.string.format_high_temp else Res.string.format_high_temp_f, weatherData.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature)
                minValueTv.text = resources.getString(if (viewModel.exists) Res.string.format_low_temp else Res.string.format_low_temp_F, weatherData.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature)
                windValueTv.text = resources.getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
                rainValueTv.text = resources.getString(Res.string.format_rain, weatherData.currentWeatherData.currentRain)
                visibilityValueTv.text = getString(Res.string.format_visibility, weatherData.currentWeatherData.currentVisibility/1000)
                realFeelValueTv.text = getString(if (viewModel.exists) Res.string.format_current_weather else Res.string.format_current_weather_f, weatherData.currentWeatherData.currentFeelsLike)
                humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
                pressureValueTv.text = getString(Res.string.format_air_pressure, weatherData.currentWeatherData.currentPressure.toString())
                uviValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)
            }

            sunriseSunsetIncl.apply {
                sunriseValueTv.text = convertLongToDateTime(weatherData.dailyWeatherData[0].dailySunrise, DateTimeFormat.outputHMA)
                sunsetValueTv.text = convertLongToDateTime(weatherData.dailyWeatherData[0].dailySunSet, DateTimeFormat.outputHMA)
            }

            setSunriseSunsetProgress(weatherData.dailyWeatherData[0].dailySunrise, weatherData.dailyWeatherData[0].dailySunSet)
        }
    }

    private fun setSunriseSunsetProgress(sunrise: Long, sunset: Long) {
        binding.apply {
            val max = (sunset - sunrise).toInt()
            sunriseSunsetIncl.apply {
                sunRiseSetPb.setMaxIndicatorValue(max)
                sunRiseSetPb.setMinIndicatorValue(0)
                sunRiseSetPb.setIndicatorValue(calculateProgressBySunriseSunset(sunrise, sunset))
            }
        }
    }

    private fun onClickListener(){
        binding.apply {
            airQualityIncl.customIndicatorView.clickWithDebounce {
                AirQualityBottomSheet(viewModel.aqi[0]).show(childFragmentManager, "AirQualityBottomSheet")
            }
        }
    }

    private fun showAirQuality(aqi: AirQualityIndexApiEntity) {
        with(binding.airQualityIncl) {
            aqiValueTv.text = aqi.aqi.toString()
            customIndicatorView.setIndicatorValue(aqi.aqi)
            val matchingAqi = AppConstants.aqiValues.find {
                aqi.aqi == it.aqi
            }
            aqiDescriptionTv.text = matchingAqi?.name
            matchingAqi?.name.let {
                aqiValueTv.setTextColor(ContextCompat.getColor(requireContext(), getColorForAqiName(it.toString())))
            }
        }
    }

    private fun getWeatherApiParams(): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = args.lat,
            lon = args.lon,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = viewModel.units
        )
    }

    private fun getAqiParams(): AirQualityIndexApiUseCase.Params {
        return AirQualityIndexApiUseCase.Params(
            lat = args.lat,
            lon = args.lon,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = viewModel.units
        )
    }
}