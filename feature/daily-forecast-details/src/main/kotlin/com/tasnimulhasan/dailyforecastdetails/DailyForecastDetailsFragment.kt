package com.tasnimulhasan.dailyforecastdetails

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
import com.tasnimulhasan.dailyforecastdetails.databinding.FragmentDailyForecastDetailsBinding
import com.tasnimulhasan.entity.daily.DailyForecast
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class DailyForecastDetailsFragment : BaseFragment<FragmentDailyForecastDetailsBinding>() {

    @Inject lateinit var gson: Gson
    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    private val args by navArgs<DailyForecastDetailsFragmentArgs>()
    private val jsonArgs: DailyForecast by lazy {
        gson.fromJson(
            args.forecast.decode(),
            DailyForecast::class.java
        )
    }
    override fun viewBindingLayout() = FragmentDailyForecastDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        initToolbar()
        showDetails()
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_daily_forecasts_details, convertLongToDateTime(jsonArgs.dateTime, DateTimeFormat.DAILY_TIME_FORMAT))
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
            dayDateTv.text = convertLongToDateTime(jsonArgs.dateTime, DateTimeFormat.FULL_DAY_DATE)
            overViewValueTv.text = jsonArgs.weather[0].description

            tempDayValueTv.text = getString(if (exits) Res.string.format_day_weather else Res.string.format_day_weather_f, jsonArgs.temp.dayTemp)
            tempNightValueTv.text = getString(if (exits) Res.string.format_night_weather else Res.string.format_night_weather_f, jsonArgs.temp.nightTemp)
            tempMorningValueTv.text = getString(if (exits) Res.string.format_morn_weather else Res.string.format_morn_weather_f, jsonArgs.temp.mornTemp)
            tempEveValueTv.text = getString(if (exits) Res.string.format_eve_weather else Res.string.format_eve_weather_f, jsonArgs.temp.eveTemp)
            tempMaxValueTv.text = getString(if (exits) Res.string.format_daily_max_weather else Res.string.format_daily_max_weather_f, jsonArgs.temp.maxTemp)
            tempMinValueTv.text = getString(if (exits) Res.string.format_daily_min_weather else Res.string.format_daily_min_weather_f, jsonArgs.temp.minTemp)

            tempDayFlValueTv.text = getString(if (exits) Res.string.format_day_weather else Res.string.format_day_weather_f, jsonArgs.feelsLike.dayFeelsLike)
            tempNightFlValueTv.text = getString(if (exits) Res.string.format_night_weather else Res.string.format_night_weather_f, jsonArgs.feelsLike.nightFeelsLike)
            tempMorningFlValueTv.text = getString(if (exits) Res.string.format_morn_weather else Res.string.format_morn_weather_f, jsonArgs.feelsLike.mornFeelsLike)
            tempEveFlValueTv.text = getString(if (exits) Res.string.format_eve_weather else Res.string.format_eve_weather_f, jsonArgs.feelsLike.eveFeelsLike)

            pressureValueTv.text = getString(Res.string.format_air_pressure, jsonArgs.pressure.toString())
            humidityValueTv.text = getString(Res.string.format_humidity, jsonArgs.humidity.toString())
            windSpeedValueTv.text = resources.getString(Res.string.format_wind, jsonArgs.speed)
            windGustValueTv.text = getString(Res.string.format_wind_gust, jsonArgs.gust)
            windAngleValueTv.text = getString(Res.string.format_wind_degree, jsonArgs.deg)
            rainValueTv.text = resources.getString(Res.string.format_rain, jsonArgs.rain)
            cloudsValueTv.text = resources.getString(Res.string.format_rain, jsonArgs.clouds.toDouble())

            sunriseSunsetIncl.apply {
                sunriseValueTv.text = convertLongToDateTime(jsonArgs.sunrise.toLong(), DateTimeFormat.outputHMA)
                sunsetValueTv.text = convertLongToDateTime(jsonArgs.sunset.toLong(), DateTimeFormat.outputHMA)
            }

            setSunriseSunsetProgress(jsonArgs.sunrise.toLong(), jsonArgs.sunset.toLong())
        }
    }

    private fun setSunriseSunsetProgress(sunrise: Long, sunset: Long) {
        binding.apply {
            val max = (sunset - sunrise).toInt()
            val progress = calculateProgressBySunriseSunset(sunrise, sunset)
            sunriseSunsetIncl.apply {
                sunRiseSetPb.setMaxIndicatorValue(max)
                sunRiseSetPb.setMinIndicatorValue(0)
                sunRiseSetPb.setIndicatorValue(progress)

                if (progress in 1..max) bottomView.setBackgroundResource(Res.drawable.sunset_sunrise_bg_1)
                else bottomView.setBackgroundResource(Res.drawable.sunset_sunrise_bg_2)
            }
        }
    }
}