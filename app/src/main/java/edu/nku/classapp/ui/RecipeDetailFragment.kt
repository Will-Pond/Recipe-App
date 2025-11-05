package edu.nku.classapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import edu.nku.classapp.R
import edu.nku.classapp.databinding.FragmentRecipeDetailBinding
import edu.nku.classapp.viewmodel.RecipeDetailViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private val recipeDetailViewModel: RecipeDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        recipeDetailViewModel.fillData(arguments?.getString(BUNDLE_ID) ?: "")
        setUpObservers()
        return binding.root
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            recipeDetailViewModel.state.collect { event ->
                when (event) {
                    RecipeDetailViewModel.RecipeState.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.errorMessage.isVisible = true
                        binding.recipeImage.isVisible = false
                        binding.recipeNameDetail.isVisible = false
                        binding.recipeTime.isVisible = false
                        binding.recipeAuthor.isVisible = false
                        binding.recipeDescription.isVisible = false
                        binding.recipeIngredientsDetail.isVisible = false
                        binding.recipeStepsDetail.isVisible = false
                    }

                    RecipeDetailViewModel.RecipeState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.errorMessage.isVisible = false
                        binding.recipeImage.isVisible = false
                        binding.recipeNameDetail.isVisible = false
                        binding.recipeTime.isVisible = false
                        binding.recipeAuthor.isVisible = false
                        binding.recipeDescription.isVisible = false
                        binding.recipeIngredientsDetail.isVisible = false
                        binding.recipeStepsDetail.isVisible = false
                    }

                    is RecipeDetailViewModel.RecipeState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.errorMessage.isVisible = false
                        binding.recipeImage.isVisible = true
                        binding.recipeNameDetail.isVisible = true
                        binding.recipeTime.isVisible = true
                        binding.recipeAuthor.isVisible = true
                        binding.recipeDescription.isVisible = true
                        binding.recipeIngredientsDetail.isVisible = true
                        binding.recipeStepsDetail.isVisible = true
                        Glide.with(binding.root).load(event.recipe.image_ref)
                            .into(binding.recipeImage)
                        binding.recipeNameDetail.text =
                            binding.root.context.getString(R.string.recipe_name, event.recipe.title)
                        binding.recipeTime.text = binding.root.context.getString(
                            R.string.recipe_time,
                            event.recipe.time_estimate
                        )
                        binding.recipeAuthor.text = binding.root.context.getString(
                            R.string.recipe_author,
                            event.recipe.author
                        )
                        binding.recipeDescription.text = binding.root.context.getString(
                            R.string.recipe_description,
                            event.recipe.description
                        )
                        binding.recipeIngredientsDetail.text = binding.root.context.getString(
                            R.string.ingredients,
                            event.recipe.ingredients.joinToString("\n") { "• $it" }
                        )
                        binding.recipeStepsDetail.text = binding.root.context.getString(
                            R.string.recipe_steps,
                            event.recipe.instructions.mapIndexed { index, step -> "${index + 1}. $step" }.joinToString("\n")
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val BUNDLE_ID = "title"

        fun newInstance(title: String) = RecipeDetailFragment().apply {
            arguments = bundleOf(BUNDLE_ID to title)
            Log.d("Recipe title", title)
        }
    }
}