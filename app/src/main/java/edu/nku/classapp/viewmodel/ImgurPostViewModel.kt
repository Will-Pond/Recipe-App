package edu.nku.classapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.nku.classapp.data.model.ImgurPostApiResponse
import edu.nku.classapp.data.repository.ImgurPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ImgurPostViewModel @Inject constructor(
    private val imgurRepository: ImgurPostRepository
) : ViewModel() {

    private val _imgurPostState =
        MutableStateFlow<ImgurPostState>(ImgurPostState.Loading)
    val imgurPostState: StateFlow<ImgurPostState> = _imgurPostState.asStateFlow()

    fun uploadImageToImgur(imageBase64: String, description: String, type: String, title: String) {
        viewModelScope.launch {
            _imgurPostState.value = ImgurPostState.Loading

            val imageByteArray = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
            val requestBodyImage = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageByteArray)
            val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBodyImage)

            val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
            val typePart = RequestBody.create("text/plain".toMediaTypeOrNull(), type)
            val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)

            when (val response = imgurRepository.postImage(imagePart, descriptionPart, typePart, titlePart)) {
                is ImgurPostApiResponse.Error -> {
                    _imgurPostState.value = ImgurPostState.Error
                    Log.e("ImgurPost", "Error uploading image: ${response.error}")
                    Log.e("ImgurPost", "Error uploading image:")
                    Log.e("ImgurPost", "Status code: ${response.statusCode}")
                    Log.e("ImgurPost", "Error body: ${response.errorBody}")
                }
                is ImgurPostApiResponse.Success -> {
                    val imageLink = response.data.link
                    _imgurPostState.value = ImgurPostState.Success(imageLink)
                }
            }
        }
    }

    sealed class ImgurPostState {
        data class Success(val imageLink: String) : ImgurPostState()
        object Error : ImgurPostState()
        object Loading : ImgurPostState()
    }
}
