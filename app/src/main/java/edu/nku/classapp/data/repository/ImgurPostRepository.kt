package edu.nku.classapp.data.repository

import edu.nku.classapp.data.model.ImgurPostApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ImgurPostRepository {
    suspend fun postImage(
        image: MultipartBody.Part,
        description: RequestBody,
        type: RequestBody,
        title: RequestBody
    ): ImgurPostApiResponse
}
