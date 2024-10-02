package com.tasnimulhasan.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.city.databinding.ItemCitiesBinding
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.roundToPrecision
import com.tasnimulhasan.common.extfun.setTextColorWithIconBG
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import com.tasnimulhasan.designsystem.R as Res

class CityListAdapter(
    private val exists: Boolean,
    private val onClick: (CityListRoomEntity) -> Unit,
    private val onLongClick: (CityListRoomEntity) -> Unit
) : DataBoundListAdapter<CityListRoomEntity, ItemCitiesBinding>(
    diffCallback = object : DiffUtil.ItemCallback<CityListRoomEntity>() {
        override fun areItemsTheSame(oldItem: CityListRoomEntity, newItem: CityListRoomEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CityListRoomEntity,
            newItem: CityListRoomEntity
        ) =
            oldItem == newItem
    }
) {
    private var weatherMap: Map<Pair<Double, Double>, WeatherApiEntity> = mapOf()

    override fun createBinding(parent: ViewGroup) = ItemCitiesBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemCitiesBinding, item: CityListRoomEntity, position: Int) {
        with(binding) {
            cityNameTv.text = item.cityName
            val key = Pair(roundToPrecision(item.lat?:0.0, 2), roundToPrecision(item.lon?:0.0, 2))
            val weatherData = weatherMap[key]
            if (weatherData != null) {
                cityTempTv.text = root.context.getString(if (exists) Res.string.format_temperature else Res.string.format_temperature_f, weatherData.currentWeatherData.currentTemp)
                cityConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
                cityTimeTv.text = DateTimeParser.convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.outputHMA)
                AppConstants.iconSetTwo.find { it.iconId == weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon }?.let { icon ->
                    cityIconIv.setImageResource(icon.iconRes)
                }
                cityWindTv.text = root.context.getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
                cityRainTv.text = root.context.getString(Res.string.format_rain, weatherData.currentWeatherData.currentRain)
                cityFeelsLike.text = root.context.getString(if (exists) Res.string.format_temperature else Res.string.format_temperature_f, weatherData.currentWeatherData.currentFeelsLike)
            } else {
                cityTempTv.text = null
                cityConditionTv.text = null
                cityTimeTv.text = null
                cityIconIv.setImageDrawable(null)
            }

            root.clickWithDebounce { onClick.invoke(item) }
            root.setOnLongClickListener {
                onLongClick.invoke(item)
                true
            }
        }
    }

    fun updateWeatherData(weatherList: List<WeatherApiEntity>) {
        weatherMap = weatherList.associateBy { Pair(roundToPrecision(it.lat, 2), roundToPrecision(it.lon, 2)) }
        notifyItemRangeChanged(0, itemCount)
    }
}