package edu.nku.classapp.data.model

import edu.nku.classapp.model.ImgurPostResponse

sealed class ImgurPostApiResponse {
    data class Success(val data: ImgurPostResponse.Data) : ImgurPostApiResponse()

    data class Error(
        val error: String,
        val statusCode: Int? = null,
        val errorBody: String? = null
    ) : ImgurPostApiResponse()
}
