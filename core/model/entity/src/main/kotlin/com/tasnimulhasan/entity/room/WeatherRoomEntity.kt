package com.tasnimulhasan.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_th_weather")
class WeatherRoomEntity(
    @PrimaryKey
    val id: String
)