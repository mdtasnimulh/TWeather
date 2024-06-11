package com.tasnimulhasan.domain.repository.local

import com.tasnimulhasan.domain.localusecase.city.DeleteCityLocalUseCase
import com.tasnimulhasan.domain.localusecase.city.FetchCityLocalUseCase
import com.tasnimulhasan.entity.room.CityListRoomEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRoomRepository {
    suspend fun insertCity(city: CityListRoomEntity)

    suspend fun deleteCity(params: DeleteCityLocalUseCase.Params)

    fun fetchCities(): Flow<List<CityListRoomEntity>>
}