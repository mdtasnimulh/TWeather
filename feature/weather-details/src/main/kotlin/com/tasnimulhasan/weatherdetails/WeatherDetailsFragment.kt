package com.tasnimulhasan.weatherdetails

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.domain.apiusecase.details.WeatherDetailsApiUseCase
import com.tasnimulhasan.entity.details.WeatherDetailsApiEntity
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.weatherdetails.databinding.FragmentWeatherDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class WeatherDetailsFragment : BaseFragment<FragmentWeatherDetailsBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    private val viewModel by viewModels<WeatherDetailsViewModel>()

    override fun viewBindingLayout() = FragmentWeatherDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureNsv)

        initToolbar()
        uiStateObserver()
        bindUiEvent()
        onClickListener()

        viewModel.action(UiAction.FetchWeatherOverview(getOverviewApiParams()))
    }

    private fun initToolbar() {
        binding.backIv.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private infix fun showLoader(loading: Boolean) {
        if (loading) {
            showToastMessage("Loading, Please Wait!")
        }
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> this showLoader uiState.loading
                is UiState.ApiSuccess -> this showWeatherOverview uiState.weatherOverview
                is UiState.Error -> errorHandler.dataError(uiState.message){ /*NA*/ }
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private infix fun showWeatherOverview(overview: WeatherDetailsApiEntity){
        binding.apply {
            Timber.e("WeatherOverview $overview")
        }
    }

    private fun onClickListener(){
        binding.apply {

        }
    }

    private fun getOverviewApiParams(): WeatherDetailsApiUseCase.Params {
        return WeatherDetailsApiUseCase.Params(
            lat = AppConstants.DEFAULT_LATITUDE,
            lon = AppConstants.DEFAULT_LONGITUDE,
            appid = AppConstants.OPEN_WEATHER_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}