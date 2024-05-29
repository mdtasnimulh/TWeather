package com.tasnimulhasan.city

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tasnimulhasan.city.databinding.FragmentCityBinding
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class CityFragment : BaseFragment<FragmentCityBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    private val viewModel by viewModels<CityViewModel>()

    override fun viewBindingLayout() = FragmentCityBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        initToolbar()
        uiStateObserver()
        bindUiEvent()
        onClickListener()
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_city)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
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
                is UiState.ApiSuccess -> this showCityName uiState.cityEntity
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private infix fun showCityName(result: List<CitySearchApiEntity>) {
        binding.apply {
            Timber.e("SearchedCity $result")
        }
    }

    private fun onClickListener() {
        binding.apply {
            detailsTv.clickWithDebounce {
                viewModel.action(UiAction.FetchCityName(getSearchApiParams()))
            }
        }
    }

    private fun getSearchApiParams(): CitySearchApiUseCase.Params {
        return CitySearchApiUseCase.Params(
            appId = AppConstants.OPEN_WEATHER_API_KEY,
            query = "Dhaka",
            limit = 10
        )
    }
}