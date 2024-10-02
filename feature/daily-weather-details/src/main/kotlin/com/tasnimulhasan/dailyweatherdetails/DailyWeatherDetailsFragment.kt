package com.tasnimulhasan.dailyweatherdetails

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.calculateProgressBySunriseSunset
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.decode
import com.tasnimulhasan.dailyweatherdetails.databinding.FragmentDailyWeatherDetailsBinding
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class DailyWeatherDetailsFragment : BaseFragment<FragmentDailyWeatherDetailsBinding>() {

    @Inject lateinit var gson: Gson
    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    private val args by navArgs<DailyWeatherDetailsFragmentArgs>()
    private val jsonArgs: DailyWeatherData by lazy {
        gson.fromJson(
            args.dailyWeatherData.decode(),
            DailyWeatherData::class.java
        )
    }
    override fun viewBindingLayout() = FragmentDailyWeatherDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        initToolbar()
        showDetails()
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_daily_forecasts_details, convertLongToDateTime(jsonArgs.day, DateTimeFormat.DAILY_TIME_FORMAT))
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private fun showDetails() {
        val exits =
            if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) true
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) false
            else true

        binding.apply {
            dayDateTv.text = convertLongToDateTime(jsonArgs.day, DateTimeFormat.FULL_DAY_DATE)
            overViewValueTv.text = jsonArgs.dailySummary

            tempDayValueTv.text = getString(if (exits) Res.string.format_day_weather else Res.string.format_day_weather_f, jsonArgs.dailyTemp.dailyDayTemperature)
            tempNightValueTv.text = getString(if (exits) Res.string.format_night_weather else Res.string.format_night_weather_f, jsonArgs.dailyTemp.dailyNightTemperature)
            tempMorningValueTv.text = getString(if (exits) Res.string.format_morn_weather else Res.string.format_morn_weather_f, jsonArgs.dailyTemp.dailyMorningTemperature)
            tempEveValueTv.text = getString(if (exits) Res.string.format_eve_weather else Res.string.format_eve_weather_f, jsonArgs.dailyTemp.dailyEveningTemperature)
            tempMaxValueTv.text = getString(if (exits) Res.string.format_daily_max_weather else Res.string.format_daily_max_weather_f, jsonArgs.dailyTemp.dailyMaximumTemperature)
            tempMinValueTv.text = getString(if (exits) Res.string.format_daily_min_weather else Res.string.format_daily_min_weather_f, jsonArgs.dailyTemp.dailyMinimumTemperature)

            tempDayFlValueTv.text = getString(if (exits) Res.string.format_day_weather else Res.string.format_day_weather_f, jsonArgs.dailyFeelsLike.dailyDayFeelsLike)
            tempNightFlValueTv.text = getString(if (exits) Res.string.format_night_weather else Res.string.format_night_weather_f, jsonArgs.dailyFeelsLike.dailyNightFeelsLike)
            tempMorningFlValueTv.text = getString(if (exits) Res.string.format_morn_weather else Res.string.format_morn_weather_f, jsonArgs.dailyFeelsLike.dailyMorningFeelsLike)
            tempEveFlValueTv.text = getString(if (exits) Res.string.format_eve_weather else Res.string.format_eve_weather_f, jsonArgs.dailyFeelsLike.dailyEveningFeelsLike)

            pressureValueTv.text = getString(Res.string.format_air_pressure, jsonArgs.dailyPressure.toString())
            humidityValueTv.text = getString(Res.string.format_humidity, jsonArgs.dailyHumidity.toString())
            windSpeedValueTv.text = resources.getString(Res.string.format_wind, jsonArgs.dailyWindSpeed)
            windGustValueTv.text = getString(Res.string.format_wind_gust, jsonArgs.dailyWindGust)
            windAngleValueTv.text = getString(Res.string.format_wind_degree, jsonArgs.dailyWindDegree)
            rainValueTv.text = resources.getString(Res.string.format_rain, jsonArgs.dailyRain)
            cloudsValueTv.text = resources.getString(Res.string.format_rain, jsonArgs.dailyClouds.toDouble())

            sunriseSunsetIncl.apply {
                sunriseValueTv.text = convertLongToDateTime(jsonArgs.dailySunrise, DateTimeFormat.outputHMA)
                sunsetValueTv.text = convertLongToDateTime(jsonArgs.dailySunSet, DateTimeFormat.outputHMA)
            }

            setSunriseSunsetProgress(jsonArgs.dailySunrise, jsonArgs.dailySunSet)
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
}