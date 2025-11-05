package edu.nku.classapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.nku.classapp.R
import edu.nku.classapp.databinding.RecipeCardViewBinding
import edu.nku.classapp.model.Recipe


class CookbookRecipeAdapter(
    private val onRecipeClicked: (recipe: Recipe, position: Int) -> Unit
) : RecyclerView.Adapter<CookbookRecipeAdapter.CookbookViewHolder>() {

    inner class CookbookViewHolder(
        private val binding: RecipeCardViewBinding,
        private val onRecipeClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onRecipeClicked(adapterPosition)
            }
        }

        fun bind(recipe: Recipe) {
            binding.recipeName.text =
                binding.root.context.getString(R.string.recipe_name, recipe.title)
            binding.recipeTime.text =
                binding.root.context.getString(R.string.recipe_time, recipe.time_estimate)
            binding.recipeIngredients.text =
                binding.root.context.getString(
                    R.string.ingredients,
                    recipe.ingredients.joinToString(", ")
                )

            Glide.with(binding.root)
                .load(recipe.image_ref)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.recipeImage)

            Glide.with(binding.root)
                .load(
                    if (recipe.isFavorite) R.drawable.baseline_favorite_24
                    else R.drawable.baseline_favorite_border_24
                )
                .into(binding.favoriteButton)


            binding.favoriteButton.setOnClickListener {
                recipe.isFavorite = !recipe.isFavorite
                notifyItemChanged(adapterPosition)
            }
        }
    }

    private val recipes = mutableListOf<Recipe>()

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newRecipes: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookbookViewHolder {
        val binding = RecipeCardViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CookbookViewHolder(binding) { position ->
            onRecipeClicked(recipes[position], position)
        }
    }

    override fun onBindViewHolder(holder: CookbookViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size
}
