package edu.nku.classapp.model

data class Recipe(
    val title: String = "",
    val image_ref: String = "",
    val author: String = "",
    val time_estimate: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    var isFavorite: Boolean = false
)
