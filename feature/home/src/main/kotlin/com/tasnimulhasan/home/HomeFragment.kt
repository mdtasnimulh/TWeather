package com.tasnimulhasan.home

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.location.Geocoder
import android.os.Build
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
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.dateparser.DateTimeFormat
import com.tasnimulhasan.common.dateparser.DateTimeParser.convertLongToDateTime
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.createLocationRequest
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

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)-> {
                if (requireActivity().isLocationEnabled()) viewModel.isLocationGranted.value = true
                else {
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

        setUnit()
        checkCache()
    }

    private fun checkCache() {
        if (sharedPrefHelper.getString(SpKey.CURRENT_TIME).isNotEmpty()) {
            if (System.currentTimeMillis() - sharedPrefHelper.getString(SpKey.CURRENT_TIME).toLong() > 1800000) {
                requestPermission()
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
            requestPermission()
            viewModel.isLocationGranted.execute {
                if (it) {
                    getCurrentLocation()
                    initUi()
                }
            }
        }
    }

    private fun setUnit() {
        if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS)
            viewModel.units = AppConstants.DATA_UNIT_CELSIUS
        else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT)
            viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
        else
            viewModel.units = AppConstants.DATA_UNIT_CELSIUS
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

                fetchData()
            } catch (_: Exception) {}
        }
    }

    private fun getCityName(lat: Double, lon: Double) {
        val place = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lon, 1)?.get(0)
        try {
            viewModel.cityName = place?.subLocality ?: place?.thoroughfare ?: ""
            viewModel.locality = place?.locality ?: ""
            sharedPrefHelper.putString(SpKey.CITY_NAME, viewModel.cityName)
            sharedPrefHelper.putString(SpKey.LOCALITY_NAME, viewModel.locality)
        } catch (e: IOException) { e.printStackTrace() }
    }

    private fun initUi() {
        uiStateObserver()
        bindUiEvent()
        onClickListener()
    }

    private fun fetchData() {
        viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
        viewModel.action(UiAction.FetchWeatherOverview(viewModel.getOverviewApiParams()))
        viewModel.action(UiAction.FetchAirQualityIndex(viewModel.getAqiParams()))
    }

    private infix fun initDailyRecyclerView(dailyWeatherData: List<DailyWeatherData>) {
        dailyAdapter = DailyAdapter(sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) {
            navigateToDestination(getString(UI.string.deep_link_daily_forecast_fragment_args, viewModel.locality).toUri())
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
                    this initDailyRecyclerView uiState.weatherData.dailyWeatherData.take(5)
                }
                is UiState.AirQualityIndex -> this showAirQualityIndex uiState.aqi
                is UiState.WeatherOverview -> binding.summaryTv.text = uiState.weatherOverview.weatherOverview
            }
        }
    }

    private infix fun showWeatherData(weatherData: WeatherApiEntity) {
        binding.apply {
            setCurrentWeatherIcon(weatherData.currentWeatherData.currentWeatherCondition)
            unitTv?.text = if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) AppConstants.SHORT_FORM_CELSIUS
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) AppConstants.SHORT_FORM_FAHRENHEIT
            else AppConstants.SHORT_FORM_CELSIUS
            cityNameTv.text = viewModel.cityName
            tempTv.text = getString(Res.string.format_temp, weatherData.currentWeatherData.currentTemp)
            currentConditionTv.text = weatherData.currentWeatherData.currentWeatherCondition[0].currentWeatherCondition.map { "$it\n" }.joinToString("")
            dayTimeTv.text = convertLongToDateTime(weatherData.currentWeatherData.currentTime, DateTimeFormat.DAY_HOUR_TIME_FORMAT  )
            humidityValueTv.text = getString(Res.string.format_humidity, weatherData.currentWeatherData.currentHumidity.toString())
            windValueTv.text = getString(Res.string.format_wind, weatherData.currentWeatherData.currentWindSpeed)
            uviValueTv.text = getString(Res.string.format_uv_index, weatherData.currentWeatherData.currentUvi)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                val blur: RenderEffect = RenderEffect.createBlurEffect(5f, 5f, Shader.TileMode.MIRROR)
                weatherConditionIv.setRenderEffect(blur)
            }
        }
    }

    private infix fun showAirQualityIndex(aqi: List<AirQualityIndexApiEntity>) {
        with(binding.airQualityIncl) {
            aqiValueTv.text = aqi[0].aqi.toString()
            customIndicatorView.setIndicatorValue(aqi[0].aqi)
            val matchingAqi = AppConstants.aqiValues.find { aqi[0].aqi == it.aqi }
            matchingAqi?.let {
                aqiDescriptionTv.text = it.name
                aqiValueTv.setTextColor(ContextCompat.getColor(requireContext(), getColorForAqiName(it.name)))
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute {  }
    }

    private fun onClickListener() {
        binding.apply {
            seeDetailsTv.clickWithDebounce { navigateToDestination(getString(UI.string.deep_link_weather_details_fragment_args, binding.cityNameTv.text.toString(), viewModel.latitude, viewModel.longitude).toUri()) }
            seeMoreDailyTempTv.clickWithDebounce { navigateToDestination(getString(UI.string.deep_link_daily_forecast_fragment_args, viewModel.locality).toUri()) }
            cityIv.clickWithDebounce { navigateToDestination(getString(UI.string.deep_link_city_fragment).toUri()) }
            airQualityIncl.customIndicatorView.clickWithDebounce { AirQualityBottomSheet(viewModel.aqi[0]).show(childFragmentManager, "AirQualityBottomSheet") }
            unitTv?.clickWithDebounce {
                if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) {
                    sharedPrefHelper.putString(SpKey.UNIT_TYPE, AppConstants.DATA_UNIT_FAHRENHEIT)
                    viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
                } else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) {
                    sharedPrefHelper.putString(SpKey.UNIT_TYPE, AppConstants.DATA_UNIT_CELSIUS)
                    viewModel.units = AppConstants.DATA_UNIT_CELSIUS
                } else {
                    sharedPrefHelper.putString(SpKey.UNIT_TYPE, AppConstants.DATA_UNIT_FAHRENHEIT)
                    viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
                }
                viewModel.action(UiAction.FetchWeatherData(viewModel.getWeatherApiParams()))
                viewModel.action(UiAction.FetchWeatherOverview(viewModel.getOverviewApiParams()))
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