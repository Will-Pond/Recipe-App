package edu.nku.classapp.data.repository

import edu.nku.classapp.data.model.ImgurPostApiResponse
import edu.nku.classapp.data.api.ImgurApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class ImgurPostRepositoryReal @Inject constructor(
    private val imgurApi: ImgurApi
) : ImgurPostRepository {

    private val clientId = "4882244d93cda91"

    override suspend fun postImage(image: MultipartBody.Part, description: RequestBody, type: RequestBody, title: RequestBody): ImgurPostApiResponse {
        return try {

            val response = imgurApi.uploadImage(clientId, image, type, title, description)

            if (response.success) {
                ImgurPostApiResponse.Success(response.data)
            } else {
                ImgurPostApiResponse.Error("Image upload failed, response not successful.")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = "HTTP error occurred. Code: ${e.code()}, Message: ${e.message()}, Body: $errorBody"
            ImgurPostApiResponse.Error(errorMessage)
        } catch (e: Exception) {
            ImgurPostApiResponse.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
}
