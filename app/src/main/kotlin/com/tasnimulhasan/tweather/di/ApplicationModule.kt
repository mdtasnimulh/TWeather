package com.tasnimulhasan.tweather.di

import android.content.Context
import com.tasnimulhasan.common.utils.Utils
import com.tasnimulhasan.di.qualifier.AppBuildType
import com.tasnimulhasan.di.qualifier.AppVersion
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.tweather.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun sharePrefHelper(@ApplicationContext context: Context): SharedPrefHelper =
        SharedPrefHelper(context)

    @Provides
    @Singleton
    @AppBuildType
    fun provideBuildType() = Utils.getBuildTypeName(BuildConfig.BUILD_TYPE)

    @Provides
    @Singleton
    @AppVersion
    fun provideVersion() = BuildConfig.VERSION_NAME

}