package com.tasnimulhasan.dailyforecast

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
import com.tasnimulhasan.dailyforecast.databinding.ItemDailyForecastBinding
import com.tasnimulhasan.entity.daily.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tasnimulhasan.designsystem.R as Res

class DailyForecastAdapter(
    private val exits: Boolean,
    private val onClick: (DailyForecast) -> Unit
) : DataBoundListAdapter<DailyForecast, ItemDailyForecastBinding>(
    diffCallback = object : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast) =
            oldItem == newItem
    }
) {

    override fun createBinding(parent: ViewGroup) = ItemDailyForecastBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemDailyForecastBinding, item: DailyForecast, position: Int) {
        val context = binding.root.context
        val resources = context.resources

        with(binding) {
            dailyMinMaxTv.text = resources.getString(if (exits) Res.string.format_min_max_home_temp else Res.string.format_min_max_home_temp_f, item.temp.maxTemp, item.temp.minTemp)
            dayTv.text = convertLongToDateTime(item.dateTime, DateTimeFormat.DAILY_TIME_FORMAT)
            AppConstants.iconSetTwo.find { it.iconId == item.weather.firstOrNull()?.icon }?.let {
                dailyIconIv.setImageResource(it.iconRes)
            }
            summaryTv.text = item.weather.firstOrNull()?.description

            val isCurrentTime = convertLongToDateTime(item.dateTime, DateTimeFormat.sqlYMD) == getCurrentTimeFormatted()
            if (isCurrentTime) root.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, Res.color.white_n_gray_light_100)))
            else root.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, Res.color.background_color_white_blur)))

            root.clickWithDebounce {
                onClick.invoke(item)
            }
        }
    }

    private fun getCurrentTimeFormatted(): String {
        return SimpleDateFormat(DateTimeFormat.sqlYMD, Locale.US).format(Date(System.currentTimeMillis()))
    }
}