package com.tasnimulhasan.data.repoimpl.local

import com.tasnimulhasan.cache.dao.CityListDao
import com.tasnimulhasan.domain.localusecase.city.DeleteCityLocalUseCase
import com.tasnimulhasan.domain.repository.local.WeatherRoomRepository
import com.tasnimulhasan.entity.room.CityListRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRoomRepoImpl @Inject constructor(
    private val cityListDao: CityListDao
) : WeatherRoomRepository{
    override suspend fun insertCity(city: CityListRoomEntity) = cityListDao.insertCity(city)

    override suspend fun deleteCity(params: DeleteCityLocalUseCase.Params) = cityListDao.deleteCities(id = params.id, name = params.name)

    override fun fetchCities(): Flow<List<CityListRoomEntity>> = cityListDao.getCities()
}