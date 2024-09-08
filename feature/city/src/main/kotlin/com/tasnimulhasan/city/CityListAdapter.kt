package com.tasnimulhasan.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.city.databinding.ItemCityListBinding
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
    private val onClick: (CityListRoomEntity) -> Unit
) : DataBoundListAdapter<CityListRoomEntity, ItemCityListBinding>(
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

    override fun createBinding(parent: ViewGroup) = ItemCityListBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemCityListBinding, item: CityListRoomEntity, position: Int) {
        with(binding) {
            cityNameTv.text = item.cityName
            val key = Pair(roundToPrecision(item.lat?:0.0, 2), roundToPrecision(item.lon?:0.0, 2))
            val weatherData = weatherMap[key]
            if (weatherData != null) {
                cityTempTv.text = root.context.getString(Res.string.format_temperature, weatherData.currentWeatherData.currentTemp)
                cityConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
                cityTimeTv.text = DateTimeParser.convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.outputHMA)
                AppConstants.iconSetTwo.find { it.iconId == weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherIcon }?.let { icon ->
                    cityIconIv.setImageResource(icon.iconRes)
                    setTextColorWithIconBG(cityTempTv, cityIconIv, Palette.from(ContextCompat.getDrawable(root.context, icon.iconRes)?.toBitmap()!!).generate(), root.context)
                }
            } else {
                cityTempTv.text = null
                cityConditionTv.text = null
                cityTimeTv.text = null
                cityIconIv.setImageDrawable(null)
            }

            root.clickWithDebounce { onClick.invoke(item) }
        }
    }

    fun updateWeatherData(weatherList: List<WeatherApiEntity>) {
        weatherMap = weatherList.associateBy { Pair(roundToPrecision(it.lat, 2), roundToPrecision(it.lon, 2)) }
        notifyItemRangeChanged(0, itemCount)
    }
}