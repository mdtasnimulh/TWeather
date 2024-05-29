package com.tasnimulhasan.thweather.di

import com.tasnimulhasan.data.repoimpl.remote.WeatherRepoImpl
import com.tasnimulhasan.domain.repository.remote.HomeWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindHomeWeatherRepository(weatherRepoImpl: WeatherRepoImpl): HomeWeatherRepository

}