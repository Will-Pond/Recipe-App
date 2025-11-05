package edu.nku.classapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.nku.classapp.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<RecipeState>(RecipeState.Loading)
    val state: StateFlow<RecipeState> = _state.asStateFlow()

    fun fillData(title: String) = viewModelScope.launch {
        _state.value = RecipeState.Loading

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val recipesCollection = db.collection("recipes")

        recipesCollection.get()
            .addOnSuccessListener { result ->
                val recipeList = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipeList.add(recipe)
                }

                Log.d("List", recipeList.toString())
                val filteredList = recipeList.filter { it.title.contains(title, ignoreCase = true) }
                Log.d("List", filteredList.toString())
                _state.value = RecipeState.Success(filteredList.first())

            }.addOnFailureListener {
                _state.value = RecipeState.Failure
            }
    }

    sealed class RecipeState {
        data class Success(val recipe: Recipe) : RecipeState()
        data object Failure : RecipeState()
        data object Loading : RecipeState()
    }
}