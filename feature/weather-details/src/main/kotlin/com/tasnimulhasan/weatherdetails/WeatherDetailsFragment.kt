package com.tasnimulhasan.weatherdetails

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.ui.ErrorUiHandler
import com.tasnimulhasan.weatherdetails.databinding.FragmentWeatherDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class WeatherDetailsFragment : BaseFragment<FragmentWeatherDetailsBinding>() {

    private lateinit var errorHandler: ErrorUiHandler


    override fun viewBindingLayout() = FragmentWeatherDetailsBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        errorHandler = ErrorUiHandler(binding.errorUi, binding.featureNsv)

        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbarIncl.apply {
            toolbarTitleTv.text = getString(Res.string.label_weather_overview)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }
}