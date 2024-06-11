package com.tasnimulhasan.domain.localusecase.city

import com.tasnimulhasan.domain.localusecase.RoomSuspendableUseCaseNonReturn
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import javax.inject.Inject

class DeleteCityLocalUseCase @Inject constructor(
    private val repository: WeatherRoomRepository
) : RoomSuspendableUseCaseNonReturn<DeleteCityLocalUseCase.Params> {

    data class Params(
        val id: Int,
        val name: String
    )

    override suspend fun invoke(params: Params) = repository.deleteCity(params)
}