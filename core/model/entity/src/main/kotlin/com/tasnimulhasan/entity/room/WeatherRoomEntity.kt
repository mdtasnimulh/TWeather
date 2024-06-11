package com.tasnimulhasan.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_th_weather")
class WeatherRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String?,
    var cityName: String?,
    var lat: Double?,
    var lon: Double?,
    var country: String?,
    var state: String?
)