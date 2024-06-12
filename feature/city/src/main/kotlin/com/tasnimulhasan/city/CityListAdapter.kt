package com.tasnimulhasan.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.city.databinding.ItemCityListBinding
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import com.tasnimulhasan.designsystem.R as Res

class CityListAdapter(
    private val onClick: (CityListRoomEntity) -> Unit
) : DataBoundListAdapter<CityListRoomEntity, ItemCityListBinding>(
    diffCallback = object : DiffUtil.ItemCallback<CityListRoomEntity>() {
        override fun areItemsTheSame(oldItem: CityListRoomEntity, newItem: CityListRoomEntity) =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: CityListRoomEntity,
            newItem: CityListRoomEntity
        ) =
            oldItem == newItem
    }
) {
    private var weatherList: List<WeatherApiEntity> = listOf()

    override fun createBinding(parent: ViewGroup) = ItemCityListBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemCityListBinding, item: CityListRoomEntity, position: Int) {
        with(binding) {
            cityNameTv.text = root.context.getString(
                Res.string.format_city_name, item.cityName?.ifEmpty { item.name }, item.state)
            if (position < weatherList.size) {
                val weatherData = weatherList[position]
                cityTempTv.text = root.context.getString(Res.string.format_temperature, weatherData.currentWeatherData.currentTemp)
                cityConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition
                cityTimeTv.text = DateTimeParser.convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.HOURLY_TIME_FORMAT)
            }

            root.clickWithDebounce { onClick.invoke(item) }
        }
    }


    fun updateWeatherData(weatherList: List<WeatherApiEntity>) {
        this.weatherList = weatherList
        notifyDataSetChanged()
    }

}