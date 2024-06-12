package com.tasnimulhasan.city

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tasnimulhasan.city.databinding.FragmentCityBinding
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.navigateDestination
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.room.CityListRoomEntity
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.ui.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class CityFragment : BaseFragment<FragmentCityBinding>() {

    private val viewModel by viewModels<CityViewModel>()
    private lateinit var errorHandler: ErrorUiHandler
    private var adapter by autoCleared<CityListAdapter>()

    override fun viewBindingLayout() = FragmentCityBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        initRecyclerView()
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

    private fun initRecyclerView() {
        adapter = CityListAdapter { item ->
            requireActivity().showWarningDialog(
                title = getString(Res.string.title_warning),
                message = getString(Res.string.msg_delete_city),
                positiveBtnCallback = {
                    viewModel.action(UiAction.DeleteCities(item))
                    showToastMessage(getString(Res.string.msg_delete_city_successful, item.cityName))
                },
                negativeButtonCallback = {
                    showToastMessage(getString(Res.string.msg_delete_city_canceled, item.cityName))
                }
            )
        }

        requireContext().setUpVerticalRecyclerView(binding.cityListRv, adapter)
    }

    private infix fun showLoader(loading: Boolean) {
        binding.cityListRv.isGone = loading
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> showLoader(uiState.loading)
                is UiState.CityList -> {
                    binding.searchCityTv.isGone = uiState.cityList.isNotEmpty()
                    showCities(uiState.cityList)
                }
                is UiState.WeatherList -> {
                    Timber.e("chkWeatherDetails ${uiState.weatherList.size}")
                    adapter.updateWeatherData(uiState.weatherList)
                }

                else -> {}
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private infix fun showCities(result: List<CityListRoomEntity>) {
        binding.apply {
            adapter.submitList(result)
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }

    private fun onClickListener() {
        binding.apply {
            citySearchFab.clickWithDebounce {
                navigateDestination(CityFragmentDirections.actionCityFragmentToCitySearchFragment())
            }
        }
    }

    override fun isEnableEdgeToEdge() = false
}