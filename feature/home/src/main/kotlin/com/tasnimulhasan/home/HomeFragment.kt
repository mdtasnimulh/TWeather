package com.tasnimulhasan.home

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.domain.apiusecase.home.HomeWeatherApiUseCase
import com.tasnimulhasan.home.databinding.FragmentHomeBinding
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

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


    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> binding.helloTv.setTextColor(
                    ContextCompat.getColor(requireContext(), Res.color.color_EC250D)
                )

                is UiState.ApiSuccess -> {
                    showToastMessage(uiState.weatherData.toString())
                    binding.helloTv.setTextColor(
                        ContextCompat.getColor(requireContext(), Res.color.textColor)
                    )
                    binding.helloTv.text = "${uiState.weatherData}"
                }
            }
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
            helloTv.clickWithDebounce {
                viewModel.action(UiAction.FetchWeatherData(getWeatherApiParams()))
            }
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