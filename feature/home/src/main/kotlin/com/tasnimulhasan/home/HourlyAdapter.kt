package com.tasnimulhasan.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.entity.home.HourlyWeatherData
import com.tasnimulhasan.home.databinding.ItemHourlyBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tasnimulhasan.designsystem.R as Res

class HourlyAdapter(
    private val onClick: (HourlyWeatherData) -> Unit
) : DataBoundListAdapter<HourlyWeatherData, ItemHourlyBinding>(
    diffCallback = object : DiffUtil.ItemCallback<HourlyWeatherData>() {
        override fun areItemsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: HourlyWeatherData, newItem: HourlyWeatherData) =
            oldItem == newItem
    }
) {

    override fun createBinding(parent: ViewGroup) = ItemHourlyBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemHourlyBinding, item: HourlyWeatherData, position: Int) {
        val context = binding.root.context
        val resources = context.resources

        with(binding) {
            hourlyTimeTv.text = convertLongToDateTime(item.hourlyTime.toLong(), DateTimeFormat.HOURLY_TIME_FORMAT)
            AppConstants.iconSetTwo.find { it.iconId == item.hourlyWeatherCondition[0].hourlyWeatherIcon }?.let {
                hourlyIconIv.setImageResource(it.iconRes)
            }
            hourlyTempTv.text = resources.getString(Res.string.format_current_weather, item.hourlyTemperature)
            hourlyConditionTv.text = item.hourlyWeatherCondition[0].hourlyWeatherCondition

            val isCurrentTime = hourlyTimeTv.text == getCurrentTimeFormatted()
            val colorPrimary = ContextCompat.getColor(context, Res.color.colorPrimary)
            val colorPrimaryDark = ContextCompat.getColor(context, Res.color.colorPrimaryDark)
            val colorWhite = ContextCompat.getColor(context, Res.color.white)
            val colorSubText = ContextCompat.getColor(context, Res.color.subTextColor)
            val colorText = ContextCompat.getColor(context, Res.color.textColor)

            root.setCardBackgroundColor(if (isCurrentTime) colorPrimary else colorWhite)
            root.strokeColor = if (isCurrentTime) colorPrimaryDark else colorPrimary
            hourlyTimeTv.setTextColor(if (isCurrentTime) ColorStateList.valueOf(colorWhite) else ColorStateList.valueOf(colorSubText))
            hourlyTempTv.setTextColor(if (isCurrentTime) ColorStateList.valueOf(colorWhite) else ColorStateList.valueOf(colorText))

            root.clickWithDebounce { onClick.invoke(item) }
        }
    }

    private fun getCurrentTimeFormatted(): String {
        return SimpleDateFormat(DateTimeFormat.HOURLY_TIME_FORMAT, Locale.US).format(Date(System.currentTimeMillis()))
    }

}