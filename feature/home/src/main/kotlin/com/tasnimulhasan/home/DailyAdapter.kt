package com.tasnimulhasan.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.calculateHoursBetweenTwoTimes
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.home.databinding.ItemDailyWeatherBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tasnimulhasan.designsystem.R as Res

class DailyAdapter(
    private val onClick: (DailyWeatherData) -> Unit
) : DataBoundListAdapter<DailyWeatherData, ItemDailyWeatherBinding>(
    diffCallback = object : DiffUtil.ItemCallback<DailyWeatherData>() {
        override fun areItemsTheSame(oldItem: DailyWeatherData, newItem: DailyWeatherData) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DailyWeatherData, newItem: DailyWeatherData) =
            oldItem == newItem
    }
) {

    override fun createBinding(parent: ViewGroup) = ItemDailyWeatherBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemDailyWeatherBinding, item: DailyWeatherData, position: Int) {
        val context = binding.root.context
        val resources = context.resources

        with(binding) {
            dayTv.text = convertLongToDateTime(item.day, DateTimeFormat.DAILY_TIME_FORMAT)
            AppConstants.iconSetTwo.find { it.iconId == item.dailyWeatherCondition[0].dailyWeatherIcon }?.let {
                dailyIconIv.setImageResource(it.iconRes)
            }
            dailyTempTv.text = resources.getString(Res.string.format_current_weather, item.dailyTemp.dailyMaximumTemperature)
            /*dailyMinMaxTv.text = resources.getString(Res.string.format_min_max_temp, item.dailyTemp.dailyMinimumTemperature, item.dailyTemp.dailyMaximumTemperature)
            sunriseValueTv.text = convertLongToDateTime(item.dailySunrise, DateTimeFormat.outputHMA)
            sunsetValueTv.text = convertLongToDateTime(item.dailySunSet, DateTimeFormat.outputHMA)
            dailySummaryTv.text = item.dailySummary
            dailyHumidityTv.text = resources.getString(Res.string.format_daily_humidity, item.dailyHumidity)
            dailyRainTv.text = resources.getString(Res.string.format_daily_rain, item.dailyRain)
            dailyUviTv.text = resources.getString(Res.string.format_daily_uvi, item.dailyUvi)
            dailyConditionTv.text = resources.getString(Res.string.format_daily_condition, item.dailyWeatherCondition[0].dailyWeatherCondition)
            sunRiseSetPb.progress = 75*/

            val isCurrentTime = dayTv.text == getCurrentTimeFormatted()
            val colorPrimary = ContextCompat.getColor(context, Res.color.green_light_100)
            val colorWhite = ContextCompat.getColor(context, Res.color.white)
            val colorText = ContextCompat.getColor(context, Res.color.textColor)

            //root.setCardBackgroundColor(if (isCurrentTime) colorPrimary else colorWhite)
            dayTv.setTextColor(if (isCurrentTime) ColorStateList.valueOf(colorText) else ColorStateList.valueOf(colorText))

            root.clickWithDebounce {
                item.isVisible = !item.isVisible
                onClick.invoke(item)
                //dailyBodyCl.isVisible = item.isVisible
            }

            Timber.e("chkTimeResult ${calculateHoursBetweenTwoTimes(item.dailySunrise, item.dailySunSet)}")
        }
    }

    private fun getCurrentTimeFormatted(): String {
        return SimpleDateFormat(DateTimeFormat.DAILY_TIME_FORMAT, Locale.US).format(Date(System.currentTimeMillis()))
    }

}