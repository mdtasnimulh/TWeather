package com.tasnimulhasan.dailyforecast

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.encode
import com.tasnimulhasan.common.extfun.navigateToDestination
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.dailyforecast.databinding.FragmentDailyForecastBinding
import com.tasnimulhasan.domain.apiusecase.daily.DailyForecastApiUseCase
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res
import com.tasnimulhasan.ui.R as UI

@AndroidEntryPoint
class DailyForecastFragment : BaseFragment<FragmentDailyForecastBinding>() {

    private lateinit var errorHandler: ErrorUiHandler
    private val viewModel by viewModels<DailyForecastViewModel>()
    private var adapter by autoCleared<DailyForecastAdapter>()
    @Inject
    lateinit var gson: Gson
    private val args by navArgs<DailyForecastFragmentArgs>()

    override fun viewBindingLayout() = FragmentDailyForecastBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureUi)

        initRecyclerView()
        initToolbar()
        uiStateObserver()
        binding.msgTv.text = resources.getString(Res.string.msg_15_day_forecast, args.cityName)

        viewModel.action(UiAction.FetchDailyForecast(getDailyForecastApiParams()))
    }
    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_daily_forecasts)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = DailyForecastAdapter { dailyForecast ->
            navigateToDestination(getString(UI.string.deep_link_daily_forecast_details_fragment_args, gson.toJson(dailyForecast).encode()).toUri())
        }

        requireContext().setUpVerticalRecyclerView(binding.itemRv, adapter)
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { state ->
            when (state) {
                is UiState.Loading -> errorHandler.showProgressBarHideFeatureUi(state.loading)
                is UiState.Error -> errorHandler.dataError(state.message) { /*NA*/ }
                is UiState.DailyForecast -> {
                    adapter.submitList(state.dailyForecast.dailyList)
                    adapter.notifyItemRangeChanged(0, adapter.itemCount)
                }
            }
        }
    }

    private fun getDailyForecastApiParams(): DailyForecastApiUseCase.Params {
        return DailyForecastApiUseCase.Params(
            name = args.cityName.lowercase(),
            count = 15,
            appId = AppConstants.DAILY_FORECAST_API_KEY,
            units = AppConstants.DATA_UNIT
        )
    }
}