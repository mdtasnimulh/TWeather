package com.tasnimulhasan.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.entity.home.HomeWeatherApiEntity
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var errorHandler: ErrorUiHandler
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<HomeViewModel>()

    override fun viewBindingLayout(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        uiStateObserver()
        bindUiEvent()
        onClickListener()

        viewModel.action(UiAction.FetchWeatherData(getWeatherApiParams()))
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    this showLoader(uiState.loading)
                }

                is UiState.ApiSuccess -> {
                    this showWeatherData uiState.weatherData
                }
            }
        }
    }

    private infix fun showLoader(loading: Boolean) {
        if (loading) {
            showToastMessage("Loading, Please Wait!")
        }
    }

    private infix fun showWeatherData(weatherData: HomeWeatherApiEntity){
        binding.apply {
            showToastMessage("Weather Data Fetched!")
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { uiEvent ->
            when (uiEvent) {
                is UiEvent.ShowError -> {
                    showToastMessage(uiEvent.message)
                }
            }
        }
    }

    private fun onClickListener(){
        binding.apply {

        }
    }

    private fun getWeatherApiParams(): HomeWeatherApiUseCase.Params {
        return HomeWeatherApiUseCase.Params(
            lat = AppConstants.DEFAULT_LATITUDE,
            lon = AppConstants.DEFAULT_LONGITUDE,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}