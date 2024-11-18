package com.tasnimulhasan.home.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.getCurrentDeviceDateTime
import com.tasnimulhasan.common.extfun.getFontBitmap
import com.tasnimulhasan.home.R
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import javax.inject.Inject

class WeatherWidget2 : AppWidgetProvider() {

    /*** App Widget Size Rule => n*m => (73n - 16) x (118m - 16) ***/

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        sharedPrefHelper = SharedPrefHelper(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.weather_forecast_widget_2)

        val weatherData = sharedPrefHelper.getWeatherData()

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

        val intent = Intent(context, RefreshWeatherReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_layout_2, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
