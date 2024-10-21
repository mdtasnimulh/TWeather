package com.tasnimulhasan.home.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tasnimulhasan.common.constant.AppConstants
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
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper,
) : CoroutineWorker(context, workerParameters) {

    private val latitude = sharedPrefHelper.getString(SpKey.LATITUDE)
    private val longitude = sharedPrefHelper.getString(SpKey.LONGITUDE)

    override suspend fun doWork(): Result {
        val weatherApiResponse = fetchWeatherDataFromApi()

        if (weatherApiResponse != null) {
            sharedPrefHelper.savedWeatherData(
                sharedPrefHelper.getString(SpKey.CITY_NAME),
                weatherApiResponse.currentWeatherData.currentTemp.toString(),
                weatherApiResponse.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
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
            views.setTextViewText(R.id.current_temp, weatherData.temperature)
            views.setTextViewText(R.id.current_condition, weatherData.condition)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
