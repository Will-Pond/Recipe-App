package edu.nku.classapp.data.repository

import edu.nku.classapp.data.model.FoodApiResponse

interface FoodRepository {
    suspend fun getProduct(barcode: String): FoodApiResponse
}