package com.tasnimulhasan.domain.localusecase.city

import com.tasnimulhasan.domain.localusecase.RoomCollectableUseCase
import com.tasnimulhasan.domain.localusecase.RoomCollectableUseCaseNoParams
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.WeatherRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchCityLocalUseCase @Inject constructor(
    private val repository: WeatherRoomRepository
) : RoomCollectableUseCaseNoParams<List<WeatherRoomEntity>> {
    override suspend fun invoke(): Flow<List<WeatherRoomEntity>> {
        return repository.fetchCities()
    }
}