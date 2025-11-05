package edu.nku.classapp.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodResponse(
    @Json(name = "code")
    val code: String,
    @Json(name = "product")
    val product: Product,
    @Json(name = "status")
    val status: Int,
    @Json(name = "status_verbose")
    val statusVerbose: String
) {
    @JsonClass(generateAdapter = true)
    data class Product(
        @Json(name = "abbreviated_product_name")
        val abbreviatedProductName: String?,
        @Json(name = "generic_name")
        val genericName: String?,
        @Json(name = "generic_name_en")
        val genericNameEn: String?,
        @Json(name = "packaging_text")
        val packagingText: String?,
        @Json(name = "packaging_text_en")
        val packagingTextEn: String?,
        @Json(name = "product_name")
        val productName: String?,
        @Json(name = "product_name_en")
        val productNameEn: String?
    )
}