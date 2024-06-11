package com.tasnimulhasan.city

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tasnimulhasan.city.databinding.FragmentCityBinding
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.getTextFromEt
import com.tasnimulhasan.common.extfun.hideKeyboard
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.ui.ErrorUiHandler
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
        searchCity()
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

    private fun initRecyclerView(){
        adapter = CityListAdapter { item ->
            Timber.e("checkItemClicked $item")
        }

        requireContext().setUpVerticalRecyclerView(binding.cityListRv, adapter)
    }

    private infix fun showLoader(loading: Boolean) {
        binding.cityListRv.isGone = loading
    }

    private fun uiStateObserver() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UiState.Loading -> this showLoader uiState.loading
                is UiState.ApiSuccess -> {
                    binding.searchCityTv.isGone = true
                    this showCities uiState.cityEntity
                }
                is UiState.Error -> errorHandler.dataError(uiState.message) { /*NA*/ }
            }
        }
    }

    private fun bindUiEvent() {
        viewModel.uiEvent.execute { _ ->

        }
    }

    private infix fun showCities(result: List<CitySearchApiEntity>) {
        binding.apply {
            adapter.submitList(result)
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }

    private fun searchCity() {
        binding.citySearchEt.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (binding.citySearchEt.getTextFromEt().isNotEmpty()) {
                    initRecyclerView()
                    binding.citySearchEt.hideKeyboard()
                    viewModel.action(UiAction.FetchCityName(getSearchApiParams(binding.citySearchEt.getTextFromEt())))
                } else showToastMessage(getString(Res.string.error_empty_search_keyword))
            }
            false
        }
    }

    private fun onClickListener() {
        binding.apply {
            searchCityTv.clickWithDebounce {
                binding.citySearchTil.requestFocus()
            }

            binding.citySearchEt.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /*NA*/ }

                override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                    if ((query?.length ?: 0) >= 1)
                        viewModel.action(UiAction.FetchCityName(getSearchApiParams(query = query.toString())))
                }

                override fun afterTextChanged(s: Editable?) { /*NA*/ }
            })
        }
    }

    private fun getSearchApiParams(query: String): CitySearchApiUseCase.Params {
        return CitySearchApiUseCase.Params(
            appId = AppConstants.OPEN_WEATHER_API_KEY,
            query = query,
            limit = 10
        )
    }

    override fun isEnableEdgeToEdge() = false
}