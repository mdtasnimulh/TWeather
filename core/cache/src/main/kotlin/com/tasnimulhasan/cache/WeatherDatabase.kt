package com.tasnimulhasan.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tasnimulhasan.cache.dao.CityListDao
import com.tasnimulhasan.entity.room.CityListRoomEntity

@Database(
    entities = [
        CityListRoomEntity::class,
    ],
    version = 1, exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cartDao(): CityListDao
}