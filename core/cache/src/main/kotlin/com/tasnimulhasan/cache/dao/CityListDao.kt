package com.tasnimulhasan.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasnimulhasan.entity.room.CityListRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(items: CityListRoomEntity)

    @Query("DELETE FROM TABLE_TH_WEATHER WHERE id=:id AND name=:name")
    suspend fun deleteCities(id: Int, name: String)

    @Query("DELETE FROM table_th_weather")
    suspend fun deleteAllCities()

    @Query("SELECT * FROM table_th_weather")
    fun getCities(): Flow<List<CityListRoomEntity>>

}