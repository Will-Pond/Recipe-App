package edu.nku.classapp.data.model

import edu.nku.classapp.model.FoodResponse

sealed class FoodApiResponse {
    data class Success(val product: FoodResponse.Product) : FoodApiResponse()
    data object Error : FoodApiResponse()
}