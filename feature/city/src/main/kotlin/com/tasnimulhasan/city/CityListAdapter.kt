package com.tasnimulhasan.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.city.databinding.ItemCityListBinding
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.entity.room.CityListRoomEntity

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

    override fun createBinding(parent: ViewGroup) = ItemCityListBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemCityListBinding, item: CityListRoomEntity, position: Int) {
        with(binding) {
            cityNameTv.text = root.context.getString(
                Res.string.format_city_search_name, item.cityName?.ifEmpty { item.name }, item.state, item.country)
            latLonTv.text = root.context.getString(Res.string.format_lat_lon, item.lat, item.lon)

            root.clickWithDebounce { onClick.invoke(item) }
        }
    }

}