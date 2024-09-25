package com.tasnimulhasan.home

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.createLocationRequest
import com.tasnimulhasan.common.extfun.encode
import com.tasnimulhasan.common.extfun.getColorForAqiName
import com.tasnimulhasan.common.extfun.isLocationEnabled
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setTextColor
import com.tasnimulhasan.common.extfun.setUpHorizontalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.home.CurrentWeatherConditionData
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import okio.IOException
import timber.log.Timber
import java.util.Locale
import java.util.Spliterator
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<WeatherViewModel>()
    private var dailyAdapter by autoCleared<DailyAdapter>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var gson: Gson

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)-> {
                if (requireActivity().isLocationEnabled()) {
                    viewModel.isLocationGranted.value = true
                } else {
                    showToastMessage(getString(Res.string.msg_location_permission_denied))
                    requireActivity().createLocationRequest()
                }
            }
            else -> {
                viewModel.isLocationGranted.value = false
                showToastMessage(getString(Res.string.msg_location_permanent_denied))
            }
        }
    }

    override fun viewBindingLayout(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        Timber.e("chkCurrentTimeUsingSharedPref: ${sharedPrefHelper.getString(SpKey.CURRENT_TIME)}")

        if (sharedPrefHelper.getString(SpKey.CURRENT_TIME).isNotEmpty()) {
            if (System.currentTimeMillis() - sharedPrefHelper.getString(SpKey.CURRENT_TIME).toLong() > 1800000) {
                requestPermission()
                getCurrentLocation()
                uiStateObserver()
                bindUiEvent()
                onClickListener()
            } else {
                uiStateObserver()
                bindUiEvent()
                onClickListener()

                if (sharedPrefHelper.getString(SpKey.CITY_NAME).isEmpty()) {
                    getCityName(sharedPrefHelper.getString(SpKey.LATITUDE).toDouble(), sharedPrefHelper.getString(SpKey.LONGITUDE).toDouble())
                } else {
                    viewModel.cityName = sharedPrefHelper.getString(SpKey.CITY_NAME)
                    binding.cityNameTv.text = sharedPrefHelper.getString(SpKey.CITY_NAME)
                }

                viewModel.latitude = sharedPrefHelper.getString(SpKey.LATITUDE)
                viewModel.longitude = sharedPrefHelper.getString(SpKey.LONGITUDE)
                viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
                viewModel.action(UiAction.FetchWeatherOverview(viewModel.getOverviewApiParams()))
                viewModel.action(UiAction.FetchAirQualityIndex(viewModel.getAqiParams()))
            }
        } else {
            requestPermission()
            viewModel.isLocationGranted.execute {
                if (it) {
                    getCurrentLocation()
                    uiStateObserver()
                    bindUiEvent()
                    onClickListener()
                }
            }
        }
    }

    private fun requestPermission() {
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnCompleteListener { location ->
            try {
                sharedPrefHelper.putString(SpKey.LATITUDE, location.result.latitude.toString())
                sharedPrefHelper.putString(SpKey.LONGITUDE, location.result.longitude.toString())
                sharedPrefHelper.putString(SpKey.CURRENT_TIME, System.currentTimeMillis().toString())

                getCityName(location.result.latitude, location.result.longitude)
                viewModel.latitude = location.result.latitude.toString()
                viewModel.longitude = location.result.longitude.toString()

                viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
                viewModel.action(UiAction.FetchWeatherOverview(viewModel.getOverviewApiParams()))
                viewModel.action(UiAction.FetchAirQualityIndex(viewModel.getAqiParams()))
            } catch (_: Exception) {}
        }
    }

    private fun getCityName(lat: Double, lon: Double) {
        val place = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lon, 1)?.get(0)
        try {
            viewModel.cityName = place?.subLocality ?: place?.thoroughfare ?: ""
            sharedPrefHelper.putString(SpKey.CITY_NAME, viewModel.cityName)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initDailyRecyclerView(dailyWeatherData: List<DailyWeatherData>) {
        dailyAdapter = DailyAdapter {
            navigateToDestination(getString(UI.string.deep_link_daily_forecast_fragment_args, viewModel.cityName).toUri())
        }
        requireContext().setUpHorizontalRecyclerView(binding.dailyWeatherRv, dailyAdapter)
        dailyAdapter.submitList(dailyWeatherData)
        dailyAdapter.notifyItemRangeChanged(0, dailyAdapter.itemCount)
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> errorHandler.showProgressBarHideFeatureUi(uiState.loading)
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
                is UiState.ApiSuccess -> {
                    this showWeatherData uiState.weatherData
                    initDailyRecyclerView(uiState.weatherData.dailyWeatherData.take(5))
                }

                is UiState.AirQualityIndex -> this showAirQualityIndex uiState.aqi
                is UiState.WeatherOverview -> binding.summaryTv.text = uiState.weatherOverview.weatherOverview
            }
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity) {
        binding.apply {
            setCurrentWeatherIcon(weatherData.currentWeatherData.currentWeatherCondition)
            binding.cityNameTv.text = viewModel.cityName
            tempTv.text = getString(Res.string.format_temp, weatherData.currentWeatherData.currentTemp)
            currentConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition.map { "$it\n" }.joinToString("")
            dayTimeTv.text = convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.DAY_HOUR_TIME_FORMAT  )

            humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
            windValueTv.text = getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
            uviValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)
        }
    }

    private infix fun showAirQualityIndex(aqi: List<AirQualityIndexApiEntity>) {
        with(binding.airQualityIncl) {
            aqiValueTv.text = aqi[0].aqi.toString()
            customIndicatorView.setIndicatorValue(aqi[0].aqi)
            val matchingAqi = AppConstants.aqiValues.find {
                aqi[0].aqi == it.aqi
            }
            aqiDescriptionTv.text = matchingAqi?.name
            matchingAqi?.name.let {
                aqiValueTv.setTextColor(ContextCompat.getColor(requireContext(), getColorForAqiName(it.toString())))
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private fun onClickListener() {
        binding.apply {
            seeDetailsTv.clickWithDebounce {
                navigateToDestination(
                    getString(UI.string.deep_link_weather_details_fragment_args,
                        binding.cityNameTv.text.toString(), viewModel.latitude, viewModel.longitude
                    ).toUri()
                )
            }

            seeMoreDailyTempTv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_daily_forecast_fragment_args, viewModel.cityName).toUri())
            }

            cityIv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_city_fragment).toUri())
            }

            airQualityIncl.customIndicatorView.clickWithDebounce {
                AirQualityBottomSheet(viewModel.aqi[0]).show(childFragmentManager, "AirQualityBottomSheet")
            }
        }
    }

    private fun setCurrentWeatherIcon(currentWeatherConditionData: List<CurrentWeatherConditionData>) {
        AppConstants.iconSetTwo.find { weatherValue ->
            weatherValue.iconId == currentWeatherConditionData[0].currentWeatherIcon
        }?.iconRes?.let { icon ->
            binding.weatherConditionIv.setImageResource(icon)
            setTextColor(binding.currentConditionTv, Palette.from(ContextCompat.getDrawable(requireContext(), icon)?.toBitmap()!!).generate())
        }
    }
}