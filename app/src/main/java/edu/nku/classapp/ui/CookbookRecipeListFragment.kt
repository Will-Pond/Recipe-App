package edu.nku.classapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import edu.nku.classapp.R
import edu.nku.classapp.databinding.FragmentRecipeListBinding
import edu.nku.classapp.model.Recipe
import edu.nku.classapp.ui.adapter.CookbookRecipeAdapter
import edu.nku.classapp.viewmodel.BarcodeViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CookbookRecipeListFragment : Fragment() {
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private val cookbookAdapter = CookbookRecipeAdapter { recipe: Recipe, position ->
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, RecipeDetailFragment.newInstance(recipe.title))
            addToBackStack(null)
        }
    }

    private var originalRecipeList: List<Recipe> = emptyList()

    private val barcodeViewModel: BarcodeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        setupObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cookbookAdapter
        }

        loadRecipesFromFirestore()

        var showFavoritesOnly = false

        binding.searchBar.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (showFavoritesOnly) {
                    originalRecipeList.filter {
                        it.isFavorite && (it.title.contains(newText.orEmpty(), ignoreCase = true) ||
                                it.ingredients.any { ingredient ->
                                    ingredient.contains(newText.orEmpty(), ignoreCase = true)
                                })
                    }
                } else {
                    originalRecipeList.filter {
                        it.title.contains(newText.orEmpty(), ignoreCase = true) ||
                                it.ingredients.any { ingredient ->
                                    ingredient.contains(newText.orEmpty(), ignoreCase = true)
                                }
                    }
                }
                cookbookAdapter.refreshData(filtered)
                return true
            }
        })

        binding.favoritesFilterButton.setOnClickListener {
            showFavoritesOnly = !showFavoritesOnly
            binding.favoritesFilterButton.text =
                if (showFavoritesOnly) "Show All" else "Show Favorites"

            val filtered = if (showFavoritesOnly) {
                originalRecipeList.filter { it.isFavorite }
            } else {
                originalRecipeList
            }

            cookbookAdapter.refreshData(filtered)
        }

        binding.barcodeButton.setOnClickListener {
            scannerLauncher.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES))
        }

        binding.addButton.setOnClickListener {
            val fragment = AddNewRecipeFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container_view, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->
        val barcode = result.contents
        barcodeViewModel.fetchBarcodeData(barcode)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            barcodeViewModel.state.collect { event ->
                when (event) {
                    is BarcodeViewModel.FoodState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.errorMessage.isVisible = false
                    }

                    is BarcodeViewModel.FoodState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.errorMessage.isVisible = false
                        binding.searchBar.isIconified = false
                        binding.searchBar.setQuery(
                            event.productName,
                            false
                        )
                    }

                    is BarcodeViewModel.FoodState.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.errorMessage.isVisible = true
                    }
                }
            }
        }
    }


    private fun loadRecipesFromFirestore() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.errorMessage.isVisible = false

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val recipesCollection = db.collection("recipes")

        recipesCollection.get()
            .addOnSuccessListener { result ->
                val recipeList = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipeList.add(recipe)
                }

                originalRecipeList = recipeList
                cookbookAdapter.refreshData(recipeList)

                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = true
            }
            .addOnFailureListener { e ->
                binding.progressBar.isVisible = false
                binding.errorMessage.isVisible = true
                binding.errorMessage.text = "Error loading recipes: ${e.message}"
            }
    }
}



