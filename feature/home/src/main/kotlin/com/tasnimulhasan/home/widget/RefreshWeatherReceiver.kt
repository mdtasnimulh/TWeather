package com.tasnimulhasan.home.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class RefreshWeatherReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workRequest = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}