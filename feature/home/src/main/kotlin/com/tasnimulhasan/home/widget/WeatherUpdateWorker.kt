package com.tasnimulhasan.home.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.getCurrentDeviceDateTime
import com.tasnimulhasan.common.extfun.getFontBitmap
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.domain.base.ApiResult
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.R
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherUpdateWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper,
) : CoroutineWorker(context, workerParameters) {

    private val latitude = sharedPrefHelper.getString(SpKey.LATITUDE)
    private val longitude = sharedPrefHelper.getString(SpKey.LONGITUDE)

    override suspend fun doWork(): Result {
        val weatherApiResponse = fetchWeatherDataFromApi()

        val exists =
            if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) true
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) false
            else true

        if (weatherApiResponse != null) {
            sharedPrefHelper.savedWeatherData(
                exists,
                sharedPrefHelper.getString(SpKey.LOCALITY_NAME),
                weatherApiResponse.currentWeatherData.currentTemp.toString(),
                weatherApiResponse.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition,
                weatherApiResponse.currentWeatherData.currentWindSpeed.toString(),
                weatherApiResponse.currentWeatherData.currentRain.toString(),
                weatherApiResponse.currentWeatherData.currentFeelsLike.toString(),
                weatherApiResponse.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon,
                weatherApiResponse.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature.toString(),
                weatherApiResponse.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature.toString(),
            )
            updateWidgetOne()
            updateWidgetTwo()
        }
        return Result.success()
    }

    private suspend fun fetchWeatherDataFromApi(): WeatherApiEntity? {
        var weatherApiEntity: WeatherApiEntity? = null
        homeWeatherApiUseCase.execute(
            HomeWeatherApiUseCase.Params(
                lat = latitude,
                lon = longitude,
                units = AppConstants.DATA_UNIT_CELSIUS,
                appid = AppConstants.OPEN_WEATHER_API_KEY
            )
        ).collect {
            when (it) {
                is ApiResult.Error -> {}
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    weatherApiEntity = it.data
                }
            }
        }
        return weatherApiEntity
    }

    private fun updateWidgetOne() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(applicationContext, WeatherWidget::class.java))
        for (appWidgetId in appWidgetIds) {
            val weatherData = sharedPrefHelper.getWeatherData()
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_weather_forecast_small)

            AppConstants.iconSetTwo.find { weatherValue ->
                weatherValue.iconId == weatherData.icon
            }?.iconRes?.let { icon ->
                views.setImageViewResource(R.id.weather_icon, icon)
            }

            views.setImageViewBitmap(
                R.id.current_temp,
                getFontBitmap(
                    context,
                    context.getString(
                        if (weatherData.exists) com.tasnimulhasan.designsystem.R.string.format_temperature else com.tasnimulhasan.designsystem.R.string.format_temperature_f,
                        weatherData.temperature.toDouble()
                    ),
                    0xFFF95A37.toInt(),
                    50f,
                    "fonts/JetBrainsMono-ExtraBold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.current_time,
                getFontBitmap(
                    context,
                    getCurrentDeviceDateTime(DateTimeFormat.WIDGET_DATE_TIME_FORMAT),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    18f,
                    "fonts/JetBrainsMono-Bold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.city_name,
                getFontBitmap(
                    context,
                    weatherData.cityName,
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    14f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.current_condition,
                getFontBitmap(
                    context,
                    weatherData.condition,
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    14f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun updateWidgetTwo() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(applicationContext, WeatherWidget2::class.java))
        for (appWidgetId in appWidgetIds) {
            val weatherData = sharedPrefHelper.getWeatherData()
            val views = RemoteViews(applicationContext.packageName, R.layout.weather_forecast_widget_2)

            AppConstants.iconSetTwo.find { weatherValue ->
                weatherValue.iconId == weatherData.icon
            }?.iconRes?.let { icon ->
                views.setImageViewResource(R.id.weather_icon, icon)
            }

            views.setImageViewBitmap(
                R.id.current_temp,
                getFontBitmap(
                    context,
                    context.getString(
                        if (weatherData.exists) com.tasnimulhasan.designsystem.R.string.format_temperature else com.tasnimulhasan.designsystem.R.string.format_temperature_f,
                        weatherData.temperature.toDouble()
                    ),
                    0xFFF95A37.toInt(),
                    50f,
                    "fonts/JetBrainsMono-ExtraBold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.current_time,
                getFontBitmap(
                    context,
                    getCurrentDeviceDateTime(DateTimeFormat.WIDGET_DATE_TIME_FORMAT),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    18f,
                    "fonts/JetBrainsMono-Bold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.city_name,
                getFontBitmap(
                    context,
                    weatherData.cityName,
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    14f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.current_condition,
                getFontBitmap(
                    context,
                    weatherData.condition,
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    14f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            AppConstants.iconSetTwo.find { weatherValue ->
                weatherValue.iconId == weatherData.icon
            }?.iconRes?.let { icon ->
                views.setImageViewResource(R.id.current_temp_iv, icon)
            }

            views.setImageViewBitmap(
                R.id.current_temp_tv,
                getFontBitmap(
                    context,
                    context.getString(
                        if (weatherData.exists) com.tasnimulhasan.designsystem.R.string.format_temp else com.tasnimulhasan.designsystem.R.string.format_temp,
                        weatherData.temperature.toDouble()
                    ),
                    0xFFF95A37.toInt(),
                    60f,
                    "fonts/JetBrainsMono-ExtraBold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.current_condition_tv,
                getFontBitmap(
                    context,
                    weatherData.condition.uppercase(),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    16f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.city_name_tv,
                getFontBitmap(
                    context,
                    weatherData.cityName.uppercase(),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    30f,
                    "fonts/JetBrainsMono-Bold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.day_tv,
                getFontBitmap(
                    context,
                    getCurrentDeviceDateTime(DateTimeFormat.WIDGET_DATE_TIME_FORMAT_DAY).uppercase(),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    16f,
                    "fonts/JetBrainsMono-SemiBold.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.date_tv,
                getFontBitmap(
                    context,
                    getCurrentDeviceDateTime(DateTimeFormat.WIDGET_DATE_TIME_FORMAT_DATE).uppercase(),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    14f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.wind_tv,
                getFontBitmap(
                    context,
                    context.getString(com.tasnimulhasan.designsystem.R.string.format_widget_wind, weatherData.windSpeed),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    12f,
                    "fonts/JetBrainsMono-Regular.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.rain_tv,
                getFontBitmap(
                    context,
                    context.getString(com.tasnimulhasan.designsystem.R.string.format_rain, weatherData.rain.toFloat()),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    12f,
                    "fonts/JetBrainsMono-Regular.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.min_temp_tv,
                getFontBitmap(
                    context,
                    context.getString(
                        if (weatherData.exists) com.tasnimulhasan.designsystem.R.string.format_temp else com.tasnimulhasan.designsystem.R.string.format_temp,
                        weatherData.dayMin.toDouble()
                    ),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    18f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            views.setImageViewBitmap(
                R.id.max_temp_tv,
                getFontBitmap(
                    context,
                    context.getString(
                        if (weatherData.exists) com.tasnimulhasan.designsystem.R.string.format_temp else com.tasnimulhasan.designsystem.R.string.format_temp,
                        weatherData.dayMax.toDouble()
                    ),
                    ContextCompat.getColor(context, com.tasnimulhasan.designsystem.R.color.widget_sub_text_color),
                    18f,
                    "fonts/JetBrainsMono-Medium.ttf"
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
