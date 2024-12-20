package com.tasnimulhasan.cache

import android.app.Application
import androidx.room.Room
import com.tasnimulhasan.cache.dao.CityListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application): WeatherDatabase {
        return Room.databaseBuilder(
            application, WeatherDatabase::class.java,
            "tweather.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCartDao(database: WeatherDatabase): CityListDao = database.cartDao()
}