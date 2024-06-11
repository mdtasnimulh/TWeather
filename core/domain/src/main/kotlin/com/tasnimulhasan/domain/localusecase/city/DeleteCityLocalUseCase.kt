package com.tasnimulhasan.domain.localusecase.city

import com.tasnimulhasan.domain.localusecase.RoomSuspendableUseCaseNonReturn
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.WeatherRoomEntity
import javax.inject.Inject

class DeleteCityLocalUseCase @Inject constructor(
    private val repository: WeatherRoomRepository
) : RoomSuspendableUseCaseNonReturn<DeleteCityLocalUseCase.Params> {

    data class Params(
        val city: WeatherRoomEntity
    )

    override suspend fun invoke(params: Params) {
        return repository.deleteCities(params.city)
    }
}