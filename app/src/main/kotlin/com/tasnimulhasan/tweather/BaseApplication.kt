package com.tasnimulhasan.tweather

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.tasnimulhasan.designsystem.FontsOverride
import com.tasnimulhasan.home.widget.WeatherUpdateWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: WeatherUpdateWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        FontsOverride.setDefaultFont(this, "MONOSPACE", FontsOverride.REGULAR_FONT)
    }
}