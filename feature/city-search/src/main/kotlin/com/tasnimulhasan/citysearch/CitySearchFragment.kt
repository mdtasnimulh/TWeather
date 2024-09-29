package com.tasnimulhasan.citysearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tasnimulhasan.citysearch.databinding.FragmentCitySearchBinding
import com.tasnimulhasan.common.base.BaseFragment
import com.tasnimulhasan.common.constant.AppConstants
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.decode
import com.tasnimulhasan.common.extfun.getTextFromEt
import com.tasnimulhasan.common.extfun.hideKeyboard
import com.tasnimulhasan.common.extfun.setUpVerticalRecyclerView
import com.tasnimulhasan.common.utils.autoCleared
import com.tasnimulhasan.domain.apiusecase.city.CitySearchApiUseCase
import com.tasnimulhasan.entity.city.CitySearchApiEntity
import com.tasnimulhasan.entity.room.CityListRoomEntity
import com.tasnimulhasan.ui.ErrorUiHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tasnimulhasan.designsystem.R as Res

@AndroidEntryPoint
class CitySearchFragment : BaseFragment<FragmentCitySearchBinding>() {

    @Inject
    lateinit var gson: Gson
    private val viewModel by viewModels<CitySearchViewModel>()
    private lateinit var errorHandler: ErrorUiHandler
    private var adapter by autoCleared<CitySearchListAdapter>()
    private val args by navArgs<CitySearchFragmentArgs>()
    private val jsonArgs: List<CityListRoomEntity> by lazy {
        gson.fromJson(
            args.cities.decode(),
            Array<CityListRoomEntity>::class.java
        ).toList()
    }

    override fun viewBindingLayout() = FragmentCitySearchBinding.inflate(layoutInflater)

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
            toolbarTitleTv.text = getString(Res.string.label_add_city)
            toolbarBackIv.clickWithDebounce {
                findNavController().popBackStack()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = CitySearchListAdapter { item ->
            val isCityExists = jsonArgs.any { it.name == item.name && it.cityName == item.cityName}
            if (isCityExists) showToastMessage(getString(Res.string.msg_city_already_exists))
            else viewModel.action(UiAction.InsertCities(CityListRoomEntity(name = item.name, cityName = item.cityName, lat = item.lat, lon = item.lon, country = item.country, state = item.state)))
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
        viewModel.uiEvent.execute { event ->
            when(event){
                is UiEvent.NavigateToPreviousFragment -> {
                    findNavController().navigateUp()
                }
            }
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
        with(binding) {
            citySearchEt.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /*NA*/ }

                override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                    if ((query?.length ?: 0) >= 1) {
                        binding.searchCityTv.isGone = true
                        viewModel.action(UiAction.FetchCityName(getSearchApiParams(query = query.toString())))
                    } else binding.searchCityTv.isGone = false
                }

                override fun afterTextChanged(s: Editable?) { /*NA*/ }
            })

            citySearchTil.setEndIconOnClickListener {
                cancelSearch()
            }
        }
    }

    private fun getSearchApiParams(query: String): CitySearchApiUseCase.Params {
        return CitySearchApiUseCase.Params(
            appId = AppConstants.OPEN_WEATHER_API_KEY,
            query = query,
            limit = 10
        )
    }

    private fun cancelSearch() {
        with(binding) {
            citySearchEt.clearFocus()
            citySearchEt.text = null
            citySearchEt.hideKeyboard()
            adapter.submitList(emptyList<CitySearchApiEntity>())
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }
}