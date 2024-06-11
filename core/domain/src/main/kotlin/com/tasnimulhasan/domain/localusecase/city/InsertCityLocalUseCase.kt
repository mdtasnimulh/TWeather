package com.tasnimulhasan.domain.localusecase.city

import com.tasnimulhasan.domain.localusecase.RoomSuspendableUseCaseNonReturn
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.WeatherRoomEntity
import javax.inject.Inject

class InsertCityLocalUseCase @Inject constructor(
    private val repository: WeatherRoomRepository
) : RoomSuspendableUseCaseNonReturn<InsertCityLocalUseCase.Params> {

    data class Params(
        val city: WeatherRoomEntity
    )

    override suspend fun invoke(params: Params) {
        return repository.upsertCities(params.city)
    }

}