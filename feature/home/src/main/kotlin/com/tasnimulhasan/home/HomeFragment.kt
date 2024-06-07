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
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.createLocationRequest
import com.tasnimulhasan.common.extfun.getColorForAqiName
import com.tasnimulhasan.common.extfun.isLocationEnabled
import com.tasnimulhasan.common.extfun.loadGifImage
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setDetailsTvTextColor
import com.tasnimulhasan.common.extfun.setDetailsValueTextColor
import com.tasnimulhasan.common.extfun.setTextColor
import com.tasnimulhasan.common.extfun.setUpHorizontalRecyclerView
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.entity.home.CurrentWeatherConditionData
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.entity.home.HourlyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<HomeViewModel>()
    private var hourlyAdapter by autoCleared<HourlyAdapter>()
    private var dailyAdapter by autoCleared<DailyAdapter>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)-> {
                if (requireActivity().isLocationEnabled()) {
                    viewModel.isLocationGranted = true
                } else {
                    showToastMessage(getString(Res.string.msg_location_permission_denied))
                    requireActivity().createLocationRequest()
                }
            }
            else -> {
                viewModel.isLocationGranted = false
                showToastMessage(getString(Res.string.msg_location_permanent_denied))
            }
        }
    }

    override fun viewBindingLayout(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        requestPermission()
        getCurrentLocation()
        uiStateObserver()
        bindUiEvent()
        onClickListener()
        setDetailsTextColor()
        setImage()
    }

    private fun requestPermission() {
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnCompleteListener { location ->
            getCityName(location)
            viewModel.latitude = location.result.latitude.toString()
            viewModel.longitude = location.result.longitude.toString()
            viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
            viewModel.action(UiAction.FetchAirQualityIndex(viewModel.getAqiParams()))
        }
    }

    private fun getCityName(location: Task<Location>) {
        val place = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(location.result.latitude, location.result.longitude, 1)?.get(0)
        if (place?.subLocality.isNullOrEmpty()) binding.currentWeatherHeaderIncl.currentCityTv.text = place?.thoroughfare
        else binding.currentWeatherHeaderIncl.currentCityTv.text = place?.subLocality
    }

    private fun initRecyclerView(hourlyWeatherData: List<HourlyWeatherData>) {
        hourlyAdapter = HourlyAdapter {

        }
        requireContext().setUpHorizontalRecyclerView(binding.hourlyRv, hourlyAdapter)
        hourlyAdapter.submitList(hourlyWeatherData)
        hourlyAdapter.notifyItemRangeChanged(0, hourlyAdapter.itemCount)
    }

    private fun initDailyRecyclerView(dailyWeatherData: List<DailyWeatherData>) {
        dailyAdapter = DailyAdapter {

        }
        requireContext().setUpVerticalRecyclerView(binding.dailyRv, dailyAdapter)
        dailyAdapter.submitList(dailyWeatherData)
        dailyAdapter.notifyItemRangeChanged(0, dailyAdapter.itemCount)
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> { /*NA*/ }
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
                is UiState.ApiSuccess -> {
                    binding.loadingAnimationView.isGone = true
                    this showWeatherData uiState.weatherData
                    initRecyclerView(uiState.weatherData.hourlyWeatherData.take(24))
                    initDailyRecyclerView(uiState.weatherData.dailyWeatherData.take(3))
                }

                is UiState.AirQualityIndex -> this showAirQualityIndex uiState.aqi
            }
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity) {
        binding.apply {
            setCurrentWeatherIcon(weatherData.currentWeatherData.currentWeatherCondition)
            currentWeatherHeaderIncl.currentWeatherTv.text = getString(Res.string.format_current_weather, weatherData.currentWeatherData.currentTemp)
            currentWeatherHeaderIncl.currentWeatherConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition

            currentWeatherDetailsIncl.apply {
                maxValueTv.text = getString(Res.string.format_current_weather, weatherData.dailyWeatherData[0].dailyTemp.dailyMaximumTemperature)
                minValueTv.text = getString(Res.string.format_current_weather, weatherData.dailyWeatherData[0].dailyTemp.dailyMinimumTemperature)
                visibilityValueTv.text = getString(Res.string.format_visibility, weatherData.currentWeatherData.currentVisibility/1000)
                realFeelValueTv.text = getString(Res.string.format_current_weather, weatherData.currentWeatherData.currentFeelsLike)
                humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
                pressureValueTv.text = getString(Res.string.format_air_pressure, weatherData.currentWeatherData.currentPressure.toString())
                windValueTv.text = getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
                uvIndexValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)
                rainValueTv.text = getString(Res.string.format_rain, weatherData.currentWeatherData.currentRain)
            }
        }
    }

    private infix fun showAirQualityIndex(aqi: List<AirQualityIndexApiEntity>) {
        with(binding.airQualityIncl) {
            aqiValueTv.text = getString(Res.string.format_aqi_pm1_5_value, aqi[0].aqiDetails.pm25)
            customIndicatorView.setIndicatorValue(aqi[0].aqiDetails.pm25.roundToInt())

            val matchingAqi = AppConstants.aqiValuesUs.find {
                aqi[0].aqiDetails.pm25 in it.lowPm..it.highPm
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
            currentWeatherHeaderIncl.currentWeatherIconIv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_weather_details_fragment).toUri())
            }

            currentWeatherHeaderIncl.currentWeatherTv.clickWithDebounce {
                navigateToDestination(getString(UI.string.deep_link_city_search_fragment).toUri())
            }
        }
    }

    private fun setCurrentWeatherIcon(currentWeatherConditionData: List<CurrentWeatherConditionData>) {
        AppConstants.iconSetTwo.find { weatherValue ->
            weatherValue.iconId == currentWeatherConditionData[0].currentWeatherIcon
        }?.iconRes?.let { icon ->
            binding.currentWeatherHeaderIncl.currentWeatherIconIv.setImageResource(icon)
            setTextColor(binding.currentWeatherHeaderIncl.currentWeatherTv, Palette.from(ContextCompat.getDrawable(requireContext(), icon)?.toBitmap()!!).generate())
        }
    }

    private fun setDetailsTextColor() {
        binding.currentWeatherDetailsIncl.apply {
            maxTv.setTextColor(setDetailsTvTextColor(Res.drawable.max_temp, requireContext()))
            minTv.setTextColor(setDetailsTvTextColor(Res.drawable.max_temp, requireContext()))
            visibilityTv.setTextColor(setDetailsTvTextColor(Res.drawable.visibility, requireContext()))
            realFeelTv.setTextColor(setDetailsTvTextColor(Res.drawable.real_feel, requireContext()))
            humidityTv.setTextColor(setDetailsTvTextColor(Res.drawable.current_humidity, requireContext()))
            pressureTv.setTextColor(setDetailsTvTextColor(Res.drawable.air_pressure, requireContext()))
            uvIndexTv.setTextColor(setDetailsTvTextColor(Res.drawable.uvi, requireContext()))
            windTv.setTextColor(setDetailsTvTextColor(Res.drawable.wind, requireContext()))
            rainTv.setTextColor(setDetailsTvTextColor(Res.drawable.rain, requireContext()))

            maxValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.max_temp, requireContext()))
            minValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.max_temp, requireContext()))
            visibilityValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.visibility, requireContext()))
            realFeelValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.real_feel, requireContext()))
            humidityValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.current_humidity, requireContext()))
            pressureValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.air_pressure, requireContext()))
            uvIndexValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.uvi, requireContext()))
            windValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.wind, requireContext()))
            rainValueTv.setTextColor(setDetailsValueTextColor(Res.drawable.rain, requireContext()))
        }
    }

    private fun setImage() {
        binding.currentWeatherDetailsIncl.maxIv.loadGifImage(Res.drawable.max_temp, requireContext())
        binding.currentWeatherDetailsIncl.minIv.loadGifImage(Res.drawable.max_temp, requireContext())
        binding.currentWeatherDetailsIncl.visibilityIv.loadGifImage(Res.drawable.visibility, requireContext())
        binding.currentWeatherDetailsIncl.realFeelIv.loadGifImage(Res.drawable.real_feel, requireContext())
        binding.currentWeatherDetailsIncl.humidityIv.loadGifImage(Res.drawable.current_humidity, requireContext())
        binding.currentWeatherDetailsIncl.pressureIv.loadGifImage(Res.drawable.air_pressure, requireContext())
        binding.currentWeatherDetailsIncl.uvIndexIv.loadGifImage(Res.drawable.uvi, requireContext())
        binding.currentWeatherDetailsIncl.windIv.loadGifImage(Res.drawable.wind, requireContext())
        binding.currentWeatherDetailsIncl.rainIv.loadGifImage(Res.drawable.rain, requireContext())
    }

    override fun isEnableEdgeToEdge() = true
}