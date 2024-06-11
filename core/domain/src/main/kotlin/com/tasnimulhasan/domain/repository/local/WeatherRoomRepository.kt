package com.tasnimulhasan.domain.repository.local

import com.tasnimulhasan.entity.room.WeatherRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WeatherRoomRepository {

    suspend fun upsertCities(city: WeatherRoomEntity)

    suspend fun deleteCities(city: WeatherRoomEntity)

    suspend fun fetchCities(): Flow<List<WeatherRoomEntity>>

}