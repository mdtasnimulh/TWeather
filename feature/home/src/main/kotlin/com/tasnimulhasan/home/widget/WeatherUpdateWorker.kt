package com.tasnimulhasan.home.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tasnimulhasan.common.constant.AppConstants
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
                sharedPrefHelper.getString(SpKey.CITY_NAME),
                weatherApiResponse.currentWeatherData.currentTemp.toString(),
                weatherApiResponse.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition,
                weatherApiResponse.currentWeatherData.currentWindSpeed.toString(),
                weatherApiResponse.currentWeatherData.currentRain.toString(),
                weatherApiResponse.currentWeatherData.currentFeelsLike.toString(),
                weatherApiResponse.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon
            )
            updateWeatherWidget()
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

    private fun updateWeatherWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(applicationContext, WeatherWidget::class.java))
        for (appWidgetId in appWidgetIds) {
            val weatherData = sharedPrefHelper.getWeatherData()
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_weather_forecast_small)

            views.setTextViewText(R.id.city_name, weatherData.cityName)
            views.setTextViewText(R.id.current_condition, weatherData.condition)
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
                    50f
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
