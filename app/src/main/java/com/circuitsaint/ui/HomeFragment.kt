package com.circuitsaint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.circuitsaint.R
import com.circuitsaint.databinding.FragmentHomeBinding
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoreViewModel by viewModels()

    private lateinit var productoAdapter: ProductoAdapter
    private var productsJob: Job? = null
    private var searchJob: Job? = null
    private var currentQuery: String = ""
    private var currentCategory: String? = null

    private val allCategoryOption = "Todas"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupCategoryFilter()
        binding.errorRetryButton.setOnClickListener { reloadProducts() }
        observeProducts()
    }

    private fun setupSearchView() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300)
                currentQuery = text?.toString()?.trim().orEmpty()
                reloadProducts()
            }
        }
    }

    private fun setupCategoryFilter() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf(allCategoryOption)
        )
        binding.categorySpinner.setAdapter(adapter)
        binding.categorySpinner.setText(allCategoryOption, false)

        binding.categorySpinner.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            currentCategory = if (selected == allCategoryOption) null else selected
            reloadProducts()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val categories = viewModel.getCategories()
                adapter.clear()
                adapter.addAll(listOf(allCategoryOption) + categories)
                binding.categorySpinner.setText(allCategoryOption, false)
            } catch (e: Exception) {
                Timber.e(e, "Error cargando categorías")
            }
        }
    }

    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            onItemClick = { product ->
                val action = HomeFragmentDirections.actionHomeToProductDetail(product.id)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                viewModel.addToCart(product.id)
                Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show()
            }
        )

        productoAdapter.addLoadStateListener { loadState ->
            when (val state = loadState.refresh) {
                is LoadState.Loading -> showLoading(true)
                is LoadState.NotLoading -> {
                    showLoading(false)
                    val isEmpty = loadState.append.endOfPaginationReached && productoAdapter.itemCount == 0
                    showEmpty(isEmpty)
                    showError(false, null)
                    updateProductCount(productoAdapter.itemCount)
                }
                is LoadState.Error -> {
                    showLoading(false)
                    showEmpty(false)
                    showError(true, state.error.message ?: "Error desconocido")
                }
            }
        }

        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productsRecyclerView.adapter = productoAdapter
    }

    private fun updateProductCount(count: Int) {
        binding.productCountTextView.text = "★ $count ${if (count == 1) "PRODUCTO" else "PRODUCTOS"} ★"
    }

    private fun observeProducts() {
        productsJob?.cancel()
        productsJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getProductsStream(currentQuery, currentCategory)
                .collectLatest { pagingData ->
                    productoAdapter.submitData(pagingData)
                }
        }
    }

    private fun reloadProducts() {
        showError(false, null)
        showEmpty(false)
        observeProducts()
    }

    private fun showLoading(show: Boolean) {
        binding.loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmpty(show: Boolean) {
        binding.emptyStateGroup.visibility = if (show) View.VISIBLE else View.GONE
        binding.productsRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(show: Boolean, message: String?) {
        binding.errorStateGroup.visibility = if (show) View.VISIBLE else View.GONE
        binding.errorMessage.text = message ?: ""
        binding.productsRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productsJob?.cancel()
        searchJob?.cancel()
        _binding = null
    }
}
