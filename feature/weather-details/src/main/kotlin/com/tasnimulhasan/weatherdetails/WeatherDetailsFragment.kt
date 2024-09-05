package com.tasnimulhasan.weatherdetails

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.decode
import com.tasnimulhasan.common.extfun.getColorForAqiName
import com.tasnimulhasan.common.extfun.setUpHorizontalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.home.HourlyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.weatherdetails.databinding.FragmentWeatherDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class WeatherDetailsFragment : BaseFragment<FragmentWeatherDetailsBinding>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var errorHandler: ErrorUiHandler
    private var hourlyAdapter by autoCleared<HourlyAdapter>()
    private val args by navArgs<WeatherDetailsFragmentArgs>()
    private val weatherArgs: WeatherApiEntity by lazy {
        gson.fromJson(
            args.weatherData.decode(),
            WeatherApiEntity::class.java
        )
    }
    private val aqiArgs: AirQualityIndexApiEntity by lazy {
        gson.fromJson(
            args.airQuality.decode(),
            AirQualityIndexApiEntity::class.java
        )
    }

    override fun viewBindingLayout() = FragmentWeatherDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureNsv)

        initToolbar()
        initDailyRecyclerView(weatherArgs.hourlyWeatherData.take(24))
        showData()
        onClickListener()
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_weather_overview)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private fun initDailyRecyclerView(hourlyWeatherData: List<HourlyWeatherData>) {
        hourlyAdapter = HourlyAdapter {

        }
        requireContext().setUpHorizontalRecyclerView(binding.hourlyRv, hourlyAdapter)
        hourlyAdapter.submitList(hourlyWeatherData)
        hourlyAdapter.notifyItemRangeChanged(0, hourlyAdapter.itemCount)
    }

    private fun showData() {
        binding.apply {
            todayTempTv.text = resources.getString(Res.string.format_temperature, weatherArgs.currentWeatherData.currentTemp)
            todayConditionTv.text = weatherArgs.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
            cityDayTv.text = resources.getString(Res.string.format_city_day, args.cityName, convertLongToDateTime(weatherArgs.currentWeatherData.currentTime, DateTimeFormat.DAY_TIME_FORMAT))

            AppConstants.iconSetTwo.find { weatherValue ->
                weatherValue.iconId == weatherArgs.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon
            }?.iconRes?.let { icon ->
                binding.weatherConditionIv.setImageResource(icon)
            }

            currentWeatherDetailsIncl.apply {
                maxValueTv.text = resources.getString(Res.string.format_high_temp, weatherArgs.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature)
                minValueTv.text = resources.getString(Res.string.format_high_temp, weatherArgs.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature)
                windValueTv.text = resources.getString(Res.string.format_wind, weatherArgs.currentWeatherData.currentWindSpeed)
                rainValueTv.text = resources.getString(Res.string.format_rain, weatherArgs.currentWeatherData.currentRain)
                visibilityValueTv.text = getString(Res.string.format_visibility, weatherArgs.currentWeatherData.currentVisibility/1000)
                realFeelValueTv.text = getString(Res.string.format_current_weather, weatherArgs.currentWeatherData.currentFeelsLike)
                humidityValueTv.text = getString(Res.string.format_humidity, weatherArgs.currentWeatherData.currentHumidity.toString())
                pressureValueTv.text = getString(Res.string.format_air_pressure, weatherArgs.currentWeatherData.currentPressure.toString())
                uviValueTv.text = getString(Res.string.format_uv_index, weatherArgs.currentWeatherData.currentUvi)
            }

            sunriseSunsetIncl.apply {
                sunriseValueTv.text = convertLongToDateTime(weatherArgs.dailyWeatherData[0].dailySunrise, DateTimeFormat.outputHMA)
                sunsetValueTv.text = convertLongToDateTime(weatherArgs.dailyWeatherData[0].dailySunSet, DateTimeFormat.outputHMA)
            }

            showAirQuality(aqiArgs)
        }
    }

    private fun onClickListener(){
        binding.apply {

        }
    }

    private fun showAirQuality(aqi: AirQualityIndexApiEntity) {
        with(binding.airQualityIncl) {
            aqiValueTv.text = getString(Res.string.format_aqi_pm1_5_value, aqi.aqiDetails.pm25)
            customIndicatorView.setIndicatorValue(aqi.aqiDetails.pm25.roundToInt())

            val matchingAqi = AppConstants.aqiValuesUs.find {
                aqi.aqiDetails.pm25 in it.lowPm..it.highPm
            }
            aqiDescriptionTv.text = matchingAqi?.name
            matchingAqi?.name.let {
                aqiValueTv.setTextColor(ContextCompat.getColor(requireContext(), getColorForAqiName(it.toString())))
            }
        }
    }
}