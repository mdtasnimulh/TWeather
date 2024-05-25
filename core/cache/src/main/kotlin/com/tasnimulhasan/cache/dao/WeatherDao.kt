package com.tasnimulhasan.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.tasnimulhasan.entity.room.WeatherRoomEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(items: WeatherRoomEntity)
    // TODO need to change above name with appropriate name
}