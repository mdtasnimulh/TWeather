package com.tasnimulhasan.thweather

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.tasnimulhasan.designsystem.FontsOverride
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        FontsOverride.setDefaultFont(this, "MONOSPACE", FontsOverride.REGULAR_FONT)
    }
}