package com.tasnimulhasan.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.calculateProgressBySunriseSunset
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.encode
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setUpHorizontalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.home.CurrentWeatherConditionData
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.ui.databinding.DialogRequestPermissionBinding
import dagger.hilt.android.AndroidEntryPoint
import okio.IOException
import java.util.Locale
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    @Inject lateinit var gson: Gson
    private lateinit var errorHandler: ErrorUiHandler
    private val viewModel by viewModels<WeatherViewModel>()
    private var dailyAdapter by autoCleared<DailyAdapter>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) checkCache()
        else {
            // Permission is denied. Check if the user has selected "Don't ask again"
            if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // The user checked "Don't ask again." Show the dialog to guide them to settings.
                showPermissionRequiredDialog()
            } else {
                // Permission denied but not permanently
                showToastMessage("Location Permission Denied")
                requestLocationPermission() // Request again
            }
        }
    }

    private val openSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        checkLocationPermission()
    }

    override fun viewBindingLayout(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.exists =
            if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) true
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) false
            else true
        setUnit()

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                checkCache()
            }
            else -> {
                // Request permission directly if it hasn't been granted yet
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkCache() {
        if (sharedPrefHelper.getString(SpKey.CURRENT_TIME).isNotEmpty()) {
            if (System.currentTimeMillis() - sharedPrefHelper.getString(SpKey.CURRENT_TIME).toLong() > 1800000) {
                getCurrentLocation()
                initUi()
            } else {
                initUi()
                if (sharedPrefHelper.getString(SpKey.CITY_NAME).isEmpty())
                    getCityName(sharedPrefHelper.getString(SpKey.LATITUDE).toDouble(), sharedPrefHelper.getString(SpKey.LONGITUDE).toDouble())
                else {
                    viewModel.cityName = sharedPrefHelper.getString(SpKey.CITY_NAME)
                    viewModel.locality = sharedPrefHelper.getString(SpKey.LOCALITY_NAME)
                    binding.cityNameTv.text = sharedPrefHelper.getString(SpKey.CITY_NAME)
                }
                viewModel.latitude = sharedPrefHelper.getString(SpKey.LATITUDE)
                viewModel.longitude = sharedPrefHelper.getString(SpKey.LONGITUDE)
                fetchData()
            }
        } else {
            getCurrentLocation()
            initUi()
        }
    }

    private fun setUnit() {
        if (viewModel.exists) viewModel.units = AppConstants.DATA_UNIT_CELSIUS
        else viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
    }

    // Function to open the app settings when the user has denied permission permanently
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        openSettingsLauncher.launch(intent)  // Use the new launcher to open settings
    }

    private fun showPermissionRequiredDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogRequestPermissionBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)

        dialogBinding.denyTv.clickWithDebounce {
            requireActivity().finish()
        }

        dialogBinding.grantTv.clickWithDebounce {
            openAppSettings()
            dialog.dismiss()
        }

        dialog.show()
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { location ->
                        sharedPrefHelper.putString(SpKey.LATITUDE, location.latitude.toString())
                        sharedPrefHelper.putString(SpKey.LONGITUDE, location.longitude.toString())
                        sharedPrefHelper.putString(SpKey.CURRENT_TIME, System.currentTimeMillis().toString())

                        getCityName(location.latitude, location.longitude)
                        viewModel.latitude = location.latitude.toString()
                        viewModel.longitude = location.longitude.toString()

                        fetchData()
                    } ?: run {
                        showToastMessage("Location is unavailable")
                    }
                } else {
                    showToastMessage("Failed to get location: ${task.exception?.message}")
                }
            }
    }

    private fun getCityName(lat: Double, lon: Double) {
        val place = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lon, 1)?.get(0)
        try {
            viewModel.cityName = place?.subLocality ?: place?.locality ?: place?.thoroughfare ?: ""
            viewModel.locality = place?.locality ?: ""
            sharedPrefHelper.putString(SpKey.CITY_NAME, viewModel.cityName)
            sharedPrefHelper.putString(SpKey.LOCALITY_NAME, viewModel.locality)
        } catch (e: IOException) { e.printStackTrace() }
    }

    private fun initUi() {
        bindUiEvent()
        onClickListener()
    }

    private fun fetchData() {
        viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
        viewModel.action(UiAction.FetchWeatherOverview(viewModel.getOverviewApiParams()))
    }

    private infix fun initDailyRecyclerView(dailyWeatherData: List<DailyWeatherData>) {
        dailyAdapter = DailyAdapter(viewModel.exists) {
            navigateToDestination(getString(UI.string.deep_link_daily_weather_detail_fragment_args, gson.toJson(it).encode()).toUri())
        }
        requireContext().setUpHorizontalRecyclerView(binding.dailyWeatherRv, dailyAdapter)
        dailyAdapter.submitList(dailyWeatherData)
        dailyAdapter.notifyItemRangeChanged(0, dailyAdapter.itemCount)
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { event ->
            when (event) {
                is UiEvent.Loading -> errorHandler.showProgressBarHideFeatureUi(event.loading)
                is UiEvent.Error -> errorHandler.dataError(event.message) { /*NA*/ }
                is UiEvent.ApiSuccess -> {
                    this showWeatherData event.weatherData
                    this initDailyRecyclerView event.weatherData.dailyWeatherData.take(7)
                }
                is UiEvent.WeatherOverview -> binding.summaryValueTv?.text = event.weatherOverview.weatherOverview
                is UiEvent.RemainingTimerValue -> binding.sunriseSunsetIncl?.remainingTimeValueTv?.text = event.time
            }
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity) {
        binding.apply {
            viewModel.action(UiAction.RemainTime(weatherData.dailyWeatherData[0].dailySunrise, weatherData.dailyWeatherData[0].dailySunSet))
            setCurrentWeatherIcon(weatherData.currentWeatherData.currentWeatherCondition)

            unitTv?.text = if (viewModel.exists) AppConstants.SHORT_FORM_CELSIUS
            else AppConstants.SHORT_FORM_FAHRENHEIT

            minMaxTempTv?.text = getString(
                if (viewModel.exists) Res.string.format_min_max_home_temp else Res.string.format_min_max_home_temp_f,
                weatherData.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature,
                weatherData.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature
            )

            cityNameTv.text = viewModel.cityName
            tempTv.text = getString(Res.string.format_temp, weatherData.currentWeatherData.currentTemp)
            currentConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition.map { "$it\n" }.joinToString("")
            dayTimeTv.text = convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.DAY_HOUR_TIME_FORMAT  )
            humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
            windValueTv.text = getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
            uviValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)

            sunriseSunsetIncl?.apply {
                sunriseValueTv.text = convertLongToDateTime(weatherData.dailyWeatherData[0].dailySunrise, DateTimeFormat.outputHMA)
                sunsetValueTv.text = convertLongToDateTime(weatherData.dailyWeatherData[0].dailySunSet, DateTimeFormat.outputHMA)
            }

            setSunriseSunsetProgress(weatherData.dailyWeatherData[0].dailySunrise, weatherData.dailyWeatherData[0].dailySunSet)
        }
    }

    private fun setSunriseSunsetProgress(sunrise: Long, sunset: Long) {
        binding.apply {
            val max = (sunset - sunrise).toInt()
            val progress = calculateProgressBySunriseSunset(sunrise, sunset)
            sunriseSunsetIncl?.apply {
                sunRiseSetPb.setMaxIndicatorValue(max)
                sunRiseSetPb.setMinIndicatorValue(0)
                sunRiseSetPb.setIndicatorValue(progress)

                if (progress in 1..<max) bottomView.setBackgroundResource(Res.drawable.sunset_sunrise_bg_1)
                else bottomView.setBackgroundResource(Res.drawable.sunset_sunrise_bg_2)
            }
        }
    }

    private fun onClickListener() {
        binding.apply {
            seeDetailsTv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_weather_details_fragment_args, viewModel.cityName, viewModel.latitude, viewModel.longitude).toUri())
                viewModel.action(UiAction.StopTimer)
            }

            seeMoreDailyTempTv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_daily_forecast_fragment_args, viewModel.locality).toUri())
                viewModel.action(UiAction.StopTimer)
            }

            cityIv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_city_fragment).toUri())
                viewModel.action(UiAction.StopTimer)
            }

            unitTv?.clickWithDebounce {
                if (viewModel.exists) {
                    sharedPrefHelper.putString(SpKey.UNIT_TYPE, AppConstants.DATA_UNIT_FAHRENHEIT)
                    viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
                } else {
                    sharedPrefHelper.putString(SpKey.UNIT_TYPE, AppConstants.DATA_UNIT_CELSIUS)
                    viewModel.units = AppConstants.DATA_UNIT_CELSIUS
                }
                viewModel.exists = !viewModel.exists
                fetchData()
            }
        }
    }

    private fun setCurrentWeatherIcon(currentWeatherConditionData: List<CurrentWeatherConditionData>) {
        AppConstants.iconSetTwo.find { weatherValue ->
            weatherValue.iconId == currentWeatherConditionData[0].currentWeatherIcon
        }?.iconRes?.let { icon ->
            binding.weatherConditionIv.setImageResource(icon)
        }
    }
}