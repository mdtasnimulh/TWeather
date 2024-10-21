package com.tasnimulhasan.home.widget

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import javax.inject.Inject

class WeatherUpdateWorkerFactory @Inject constructor(
    private val homeWeatherApiUseCase: HomeWeatherApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = WeatherUpdateWorker(
        appContext,
        workerParameters,
        homeWeatherApiUseCase,
        sharedPrefHelper
    )
}