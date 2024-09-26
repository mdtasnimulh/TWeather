package com.tasnimulhasan.city

import android.content.res.Configuration
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.tasnimulhasan.city.databinding.FragmentCityBinding
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.encode
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setUpGridRecyclerView
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.entity.room.CityListRoomEntity
import com.tasnimulhasan.sharedpref.SharedPrefHelper
import com.tasnimulhasan.sharedpref.SpKey
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.ui.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class CityFragment : BaseFragment<FragmentCityBinding>() {

    @Inject lateinit var gson: Gson
    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<CityViewModel>()
    private lateinit var errorHandler: ErrorUiHandler
    private var adapter by autoCleared<CityListAdapter>()

    override fun viewBindingLayout() = FragmentCityBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        viewModel.exists =
            if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_CELSIUS) true
            else if (sharedPrefHelper.getString(SpKey.UNIT_TYPE) == AppConstants.DATA_UNIT_FAHRENHEIT) false
            else true

        setUnit()
        initRecyclerView()
        initToolbar()
        uiStateObserver()
        onClickListener()

        viewModel.action(UiAction.FetchCities)
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_city)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUnit() {
        if (viewModel.exists) viewModel.units = AppConstants.DATA_UNIT_CELSIUS
        else viewModel.units = AppConstants.DATA_UNIT_FAHRENHEIT
    }

    private fun initRecyclerView() {
        adapter = CityListAdapter (
            exists = viewModel.exists,
            onClick = { item ->
                navigateToDestination(getString(
                    UI.string.deep_link_weather_details_fragment_args,
                    item.cityName, item.lat.toString(), item.lon.toString()
                ).toUri())
            },
            onLongClick = { item ->
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
        )

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            requireContext().setUpVerticalRecyclerView(binding.cityListRv, adapter)
        else
            requireContext().setUpGridRecyclerView(binding.cityListRv, adapter, 2)
    }

    private infix fun showLoader(loading: Boolean) {
        binding.cityListRv.isGone = loading
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> showLoader(uiState.loading)
                is UiState.CityList -> {
                    binding.searchCityTv.isVisible = uiState.cityList.isEmpty()
                    showCities(uiState.cityList)
                }
                is UiState.WeatherList -> adapter.updateWeatherData(uiState.weatherList)
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
            }
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
                navigateToDestination(getString(UI.string.deep_link_city_search_fragment_args, gson.toJson(viewModel.cityList).encode()).toUri())
            }
        }
    }
}