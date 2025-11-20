package com.circuitsaint.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.circuitsaint.data.model.Product
import com.circuitsaint.databinding.FragmentHomeBinding
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StoreViewModel by viewModels()
    
    private lateinit var productoAdapter: ProductoAdapter
    
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
        observeViewModel()
    }
    
    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                // TODO: Implementar búsqueda en ViewModel
                // viewModel.searchProducts(query)
            }
        })
    }
    
    private fun setupCategoryFilter() {
        // TODO: Implementar filtro por categoría
        // val categories = listOf("Todas", "Audio", "Accesorios", "Gaming", "Computadoras", "Smartphones", "Wearables")
        // val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        // binding.categorySpinner.setAdapter(adapter)
    }
    
    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter { product ->
            // Navegar a la pantalla de detalle del producto usando Navigation Component
            val action = HomeFragmentDirections.actionHomeToProductDetail(product.id.toString())
            findNavController().navigate(action)
        }
        
        // Agregar adapter para estados de carga
        productoAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    showLoading()
                }
                is LoadState.NotLoading -> {
                    hideLoading()
                    if (loadState.append.endOfPaginationReached && productoAdapter.itemCount == 0) {
                        showEmpty()
                    } else {
                        hideEmpty()
                    }
                    updateProductCount(productoAdapter.itemCount)
                }
                is LoadState.Error -> {
                    hideLoading()
                    showError((loadState.refresh as LoadState.Error).error.message ?: "Error desconocido")
                }
            }
        }
        
        // GridLayoutManager de 2 columnas según instructivo
        binding.productsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.productsRecyclerView.adapter = productoAdapter
    }
    
    private fun updateProductCount(count: Int) {
        binding.productCountTextView.text = "★ $count ${if (count == 1) "PRODUCTO" else "PRODUCTOS"} ★"
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getProductsPaginated().collectLatest { pagingData ->
                productoAdapter.submitData(pagingData)
            }
        }
    }
    
    private fun showLoading() {
        // TODO: Agregar ProgressBar al layout si no existe
        // binding.progressBar?.visibility = View.VISIBLE
    }
    
    private fun hideLoading() {
        // TODO: Agregar ProgressBar al layout si no existe
        // binding.progressBar?.visibility = View.GONE
    }
    
    private fun showEmpty() {
        // TODO: Agregar emptyState al layout si no existe
        // binding.emptyState?.visibility = View.VISIBLE
    }
    
    private fun hideEmpty() {
        // TODO: Agregar emptyState al layout si no existe
        // binding.emptyState?.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        // TODO: Agregar errorState al layout si no existe
        // binding.errorState?.visibility = View.VISIBLE
        // binding.errorMessage?.text = message
        Timber.e("Error cargando productos: $message")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
