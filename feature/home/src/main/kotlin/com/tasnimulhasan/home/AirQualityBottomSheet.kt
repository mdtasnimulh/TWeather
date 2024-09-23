package com.tasnimulhasan.home

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tasnimulhasan.common.base.BaseBottomSheetDialogFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.entity.aqi.AirQualityIndexApiEntity
import com.tasnimulhasan.home.databinding.BottomSheetAirQualityBinding
import dagger.hilt.android.AndroidEntryPoint
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class AirQualityBottomSheet(
    private val airQuality: AirQualityIndexApiEntity
) : BaseBottomSheetDialogFragment<BottomSheetAirQualityBinding>() {

    private val bottomSheetDialog by lazy { (dialog as BottomSheetDialog) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, Res.style.BottomSheetDialogTheme)
    }

    override fun viewBindingLayout() = BottomSheetAirQualityBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        isCancelable = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        onClickListener()
        showData()
    }

    private fun onClickListener() {
        binding.apply {
            closeIv.clickWithDebounce {
                dismiss()
            }
        }
    }

    private fun showData() {
        binding.apply {
            val matchingAqi = AppConstants.aqiValues.find { airQuality.aqi == it.aqi }
            aqiValueTv.text = airQuality.aqi.toString()
            aqiStatusTv.text = getString(Res.string.label_aqi_status, matchingAqi?.name)

            aqiChartIncl.apply {
                coNormalValueTv.text = AppConstants.CO
                noNormalValueTv.text = AppConstants.NO
                no2NormalValueTv.text = AppConstants.NO2
                o3NormalValueTv.text = AppConstants.O3
                so2NormalValueTv.text = AppConstants.SO2
                pm25NormalValueTv.text = AppConstants.PM25
                pm10NormalValueTv.text = AppConstants.PM10
                nh3NormalValueTv.text = AppConstants.NH3

                coValueTv.text = airQuality.aqiDetails.carbonMonoxide.toString()
                noValueTv.text = airQuality.aqiDetails.nitrogenOxide.toString()
                no2ValueTv.text = airQuality.aqiDetails.nitrogenDioxide.toString()
                o3ValueTv.text = airQuality.aqiDetails.ozone.toString()
                so2ValueTv.text = airQuality.aqiDetails.sulfurDioxide.toString()
                pm25ValueTv.text = airQuality.aqiDetails.pm25.toString()
                pm10ValueTv.text = airQuality.aqiDetails.pm10.toString()
                nh3ValueTv.text = airQuality.aqiDetails.ammonia.toString()
            }
        }
    }
}