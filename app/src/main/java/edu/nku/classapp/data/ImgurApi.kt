package edu.nku.classapp.data.api

import edu.nku.classapp.model.ImgurPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ImgurApi {
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(
        @Header("Authorization") clientId: String,
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody
    ): ImgurPostResponse
}
