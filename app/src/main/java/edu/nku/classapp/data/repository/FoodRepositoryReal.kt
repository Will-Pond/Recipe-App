package edu.nku.classapp.data.repository

import edu.nku.classapp.data.FoodApi
import edu.nku.classapp.data.model.FoodApiResponse
import javax.inject.Inject

class FoodRepositoryReal @Inject constructor(
    private val foodApi: FoodApi
) : FoodRepository {
    override suspend fun getProduct(barcode: String): FoodApiResponse {
        val result = foodApi.getProduct(barcode)
        return if (result.isSuccessful && result.body()?.product != null) {
            FoodApiResponse.Success(result.body()!!.product)
        } else {
            FoodApiResponse.Error
        }
    }
}