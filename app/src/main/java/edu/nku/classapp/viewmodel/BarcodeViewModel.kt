package edu.nku.classapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.nku.classapp.data.model.FoodApiResponse
import edu.nku.classapp.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _state = MutableStateFlow<FoodState>(FoodState.Loading)
    val state: StateFlow<FoodState> = _state.asStateFlow()

    fun fetchBarcodeData(barcode: String) = viewModelScope.launch {
        when (val response = foodRepository.getProduct(barcode)) {
            is FoodApiResponse.Success -> {
                val productName = when {
                    !response.product.productNameEn.isNullOrEmpty() -> response.product.productNameEn
                    !response.product.genericNameEn.isNullOrEmpty() -> response.product.genericNameEn
                    !response.product.abbreviatedProductName.isNullOrEmpty() -> response.product.abbreviatedProductName
                    !response.product.packagingTextEn.isNullOrEmpty() -> response.product.packagingTextEn
                    !response.product.productName.isNullOrEmpty() -> response.product.productName
                    !response.product.genericName.isNullOrEmpty() -> response.product.genericName
                    !response.product.packagingText.isNullOrEmpty() -> response.product.packagingText
                    else -> "Ingredient not found"
                }
                _state.value =
                    FoodState.Success(productName)
            }

            is FoodApiResponse.Error -> _state.value = FoodState.Failure
        }
    }


    sealed class FoodState {
        data object Loading : FoodState()
        data class Success(val productName: String) : FoodState()
        data object Failure : FoodState()
    }

}