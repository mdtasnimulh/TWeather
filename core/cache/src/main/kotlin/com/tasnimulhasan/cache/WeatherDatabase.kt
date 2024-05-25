package com.tasnimulhasan.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tasnimulhasan.cache.dao.WeatherDao
import com.tasnimulhasan.entity.room.WeatherRoomEntity

@Database(
    entities = [
        WeatherRoomEntity::class,
    ],
    version = 1, exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cartDao(): WeatherDao
}