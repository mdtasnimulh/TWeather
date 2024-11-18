package com.tasnimulhasan.home.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.getFontBitmap
import com.tasnimulhasan.home.R
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import javax.inject.Inject

class WeatherWidget : AppWidgetProvider() {

    /*** App Widget Size Rule => n*m => (73n - 16) x (118m - 16) ***/

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        sharedPrefHelper = SharedPrefHelper(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_weather_forecast_small)

        val weatherData = sharedPrefHelper.getWeatherData()

        views.setTextViewText(R.id.city_name, weatherData.cityName)
        views.setTextViewText(R.id.current_condition, weatherData.condition)
        /*views.setTextViewText(R.id.wind_speed, context.getString(Res.string.format_wind, weatherData.windSpeed.toDouble()))
        views.setTextViewText(R.id.rain, context.getString(Res.string.format_rain, weatherData.rain.toDouble()))
        views.setTextViewText(R.id.feels_like, context.getString(Res.string.format_current_weather, weatherData.feelsLike.toDouble()))*/

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
                    if (weatherData.exists) Res.string.format_temperature else Res.string.format_temperature_f,
                    weatherData.temperature.toDouble()
                ),
                0xFFF95A37.toInt(),
                50f
            )
        )

        val intent = Intent(context, RefreshWeatherReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
