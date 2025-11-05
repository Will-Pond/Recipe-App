package edu.nku.classapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import edu.nku.classapp.R
import edu.nku.classapp.databinding.FragmentAddNewRecipeBinding
import edu.nku.classapp.viewmodel.ImgurPostViewModel
import java.io.ByteArrayOutputStream
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNewRecipeFragment : Fragment() {

    private lateinit var binding: FragmentAddNewRecipeBinding

    private val imgurPostViewModel: ImgurPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNewRecipeBinding.bind(view)

        binding.addRecipeSelectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        binding.addRecipeSubmitBtn.setOnClickListener {
            val title = binding.addRecipeTitle.text.toString()
            val author = binding.addRecipeAuthor.text.toString()
            val timeEstimate = binding.addRecipeTimeEstimate.text.toString()
            val description = binding.addRecipeDescription.text.toString()
            val ingredients = binding.addRecipeIngredients.text.toString().split(",")
            val instructions = binding.addRecipeInstructions.text.toString().split(",")

            val imageUri = binding.addRecipeSelectedImage.drawable
            if (imageUri != null) {
                val imageBase64 = convertImageToBase64(imageUri)
                imgurPostViewModel.uploadImageToImgur(imageBase64, description, "image", title)

                lifecycleScope.launch {
                    imgurPostViewModel.imgurPostState.collect { state ->
                        when (state) {
                            is ImgurPostViewModel.ImgurPostState.Success -> {
                                val imageLink = state.imageLink
                                postRecipeToDb(title, author, timeEstimate, description, ingredients, instructions, imageLink)
                            }
                            is ImgurPostViewModel.ImgurPostState.Error -> {
                                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                                postRecipeToDb(title, author, timeEstimate, description, ingredients, instructions, null)
                            }
                            is ImgurPostViewModel.ImgurPostState.Loading -> {}
                        }
                    }
                }
            }
        }
    }

    private fun postRecipeToDb(
        title: String,
        author: String,
        timeEstimate: String,
        description: String,
        ingredients: List<String>,
        instructions: List<String>,
        imageRef: String?
    ) {
        val recipeData = hashMapOf(
            "title" to title,
            "author" to author,
            "time_estimate" to timeEstimate,
            "description" to description,
            "ingredients" to ingredients,
            "instructions" to instructions,
            "image_ref" to (imageRef ?: "null")
        )

        val db = FirebaseFirestore.getInstance()
        val recipesCollection = db.collection("recipes")

        recipesCollection.add(recipeData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error adding recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun convertImageToBase64(imageUri: Drawable): String {
        val bitmap = (imageUri as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
            data?.data?.let { imageUri ->
                binding.addRecipeSelectedImage.setImageURI(imageUri)
            }
        }
    }
}
