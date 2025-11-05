package edu.nku.classapp.data

import edu.nku.classapp.model.FoodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApi {
    @GET("product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): Response<FoodResponse>
}