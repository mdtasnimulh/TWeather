package com.tasnimulhasan.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.tasnimulhasan.entity.room.WeatherRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Upsert
    suspend fun insertCities(items: WeatherRoomEntity)

    @Delete
    suspend fun deleteCities(items: WeatherRoomEntity)

    @Query("DELETE FROM table_th_weather")
    suspend fun deleteAllCities()

    @Query("SELECT * FROM table_th_weather")
    suspend fun getCities(): Flow<List<WeatherRoomEntity>>

}