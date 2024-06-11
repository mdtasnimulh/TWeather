package com.tasnimulhasan.data.repoimpl.local

import com.tasnimulhasan.cache.dao.WeatherDao
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.WeatherRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRoomRepoImpl @Inject constructor(
    private val weatherDao: WeatherDao
) : WeatherRoomRepository{

    override suspend fun upsertCities(city: WeatherRoomEntity) {
        return weatherDao.insertCities(city)
    }

    override suspend fun deleteCities(city: WeatherRoomEntity) {
        return weatherDao.deleteCities(city)
    }

    override suspend fun fetchCities(): Flow<List<WeatherRoomEntity>> {
        return weatherDao.getCities()
    }
}