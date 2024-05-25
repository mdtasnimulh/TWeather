package com.tasnimulhasan.ui

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.core.view.isVisible
import com.tasnimulhasan.ui.databinding.ErrorUiBinding
import com.tasnimulhasan.designsystem.R as Res

class ErrorUiHandler(
    private var binding: ErrorUiBinding,
    private var featureUi: View? = null,
    private var context: Context? = null,
) {
    private var networkErrorCallbackFlag = 0
    private var dataErrorCallbackFlag = 0

    init {
        binding.root.isVisible = false
        featureUi?.isVisible = true

        context?.let {

        }
    }

    fun networkError(
        networkErrorMethodFlag: Int = 0,
        networkErrorCallback: ((flag: Int) -> Unit)?
    ) {
        binding.root.background = ContextCompat.getDrawable(binding.root.context, Res.color.white)
        featureUi?.isVisible = false
        binding.root.isVisible = true
        binding.errorGroup.isVisible = true
        binding.loadingBar.isVisible = false
        binding.retryButtonTv.text = binding.root.context.getString(Res.string.button_refresh)
        binding.errorTitleTV.text =
            binding.root.context.getString(Res.string.heading_no_internet_connection)
        binding.errorMessageTv.text =
            binding.root.context.getString(Res.string.msg_no_internet_connection)

        this.networkErrorCallbackFlag = networkErrorMethodFlag

        binding.retryButtonTv.setOnClickListener {
            reset()
            networkErrorCallback?.invoke(networkErrorCallbackFlag)
        }
    }

    fun dataError(
        message: String,
        dataErrorMethodFlag: Int = 0,
        dataErrorCallback: ((flag: Int) -> Unit)?
    ) {
        binding.root.background = ContextCompat.getDrawable(binding.root.context, Res.color.white)
        featureUi?.isVisible = false
        binding.root.isVisible = true
        binding.errorGroup.isVisible = true
        binding.loadingBar.isVisible = false
        binding.errorAnimationView.setAnimation(Res.raw.data_error_anim)
        binding.errorAnimationView.playAnimation()
        binding.errorTitleTV.text = binding.root.context.getString(Res.string.heading_oops)
        binding.errorMessageTv.text = message

        this.dataErrorCallbackFlag = dataErrorMethodFlag
        binding.retryButtonTv.setOnClickListener {
            reset()
            dataErrorCallback?.invoke(dataErrorCallbackFlag)
        }
    }

    fun showProgressBar(isLoading: Boolean) {
        binding.root.background =
            ContextCompat.getDrawable(binding.root.context, Res.color.black_opt)
        reset()
        binding.root.isVisible = isLoading
        binding.errorGroup.isVisible = !isLoading
        //lockAllChildren(isLoading)
        binding.loadingBar.isVisible = isLoading
    }

    fun showProgressBarHideFeatureUi(isLoading: Boolean) {
        binding.root.background =
            ContextCompat.getDrawable(binding.root.context, Res.color.black_opt)
        binding.root.isVisible = isLoading
        featureUi?.isVisible = !isLoading
        binding.errorGroup.isVisible = !isLoading
        binding.loadingBar.isVisible = isLoading
    }

    private fun lockAllChildren(isLocked: Boolean) {
        featureUi?.allViews?.forEach {
            it.isEnabled = !isLocked
            it.isClickable = !isLocked
        }
    }

    private fun reset() {
        binding.root.isVisible = false
        featureUi?.isVisible = true
    }
}