package com.tasnimulhasan.thweather


import com.tasnimulhasan.di.qualifier.AppBaseUrl
import com.tasnimulhasan.di.qualifier.AppImageBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class BaseUrlModule{

    @Provides
    @AppBaseUrl
    fun provideBaseUrl():String = AppConstants.BASE_URL

    @Provides
    @AppImageBaseUrl
    fun provideImageBaseUrl():String = AppConstants.BASE_URL
}

