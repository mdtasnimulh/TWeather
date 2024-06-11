package com.tasnimulhasan.domain.localusecase.city

import com.tasnimulhasan.domain.localusecase.RoomCollectableUseCaseNoParams
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.CityListRoomEntity
import javax.inject.Inject

class FetchCityLocalUseCase @Inject constructor(
    private val repository: WeatherRoomRepository
) : RoomCollectableUseCaseNoParams<List<CityListRoomEntity>> {
    override suspend fun invoke() = repository.fetchCities()
}