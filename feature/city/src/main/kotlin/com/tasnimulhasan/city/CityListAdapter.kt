package com.tasnimulhasan.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.tasnimulhasan.city.databinding.ItemCityBinding
import com.tasnimulhasan.common.adapter.DataBoundListAdapter
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.designsystem.R as Res

class CityListAdapter(
    private val onClick: (CitySearchApiEntity) -> Unit
) : DataBoundListAdapter<CitySearchApiEntity, ItemCityBinding>(
    diffCallback = object : DiffUtil.ItemCallback<CitySearchApiEntity>() {
        override fun areItemsTheSame(oldItem: CitySearchApiEntity, newItem: CitySearchApiEntity) =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: CitySearchApiEntity,
            newItem: CitySearchApiEntity
        ) =
            oldItem == newItem
    }
) {

    override fun createBinding(parent: ViewGroup) = ItemCityBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    override fun bind(binding: ItemCityBinding, item: CitySearchApiEntity, position: Int) {
        with(binding) {
            cityNameTv.text = root.context.getString(
                Res.string.format_city_search_name, item.cityName.ifEmpty { item.name }, item.state, item.country)

            root.clickWithDebounce { onClick.invoke(item) }
        }
    }

}