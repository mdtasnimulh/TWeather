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
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.home.databinding.ItemDailyWeatherBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tasnimulhasan.designsystem.R as Res

class DailyAdapter(
    private val exits: Boolean,
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
            dayTv.text = convertLongToDateTime(item.day, DateTimeFormat.DAY_TIME_FORMAT)
            AppConstants.iconSetTwo.find { it.iconId == item.dailyWeatherCondition[0].dailyWeatherIcon }?.let {
                dailyIconIv.setImageResource(it.iconRes)
            }
            dailyTempTv.text = resources.getString(if (exits) Res.string.format_home_min_max_temp else Res.string.format_home_min_max_temp_f,
                item.dailyTemp.dailyMaximumTemperature, item.dailyTemp.dailyMinimumTemperature)
            dailyConditionTv.text = item.dailyWeatherCondition[0].dailyWeatherCondition

            val isCurrentTime = dayTv.text == getCurrentTimeFormatted()
            if (isCurrentTime) mainCv.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, Res.color.background_color_2)))
            else mainCv.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, Res.color.background_color_white)))

            root.setOnClickListener { onClick.invoke(item) }
        }
    }

    private fun getCurrentTimeFormatted(): String {
        return SimpleDateFormat(DateTimeFormat.DAY_TIME_FORMAT, Locale.US).format(Date(System.currentTimeMillis()))
    }

}