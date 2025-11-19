package com.circuitsaint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.circuitsaint.databinding.FragmentHomeBinding
import com.circuitsaint.viewmodel.StoreViewModel
import com.circuitsaint.viewmodel.StoreViewModelFactory

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory(requireActivity().application)
    }
    
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
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter { product ->
            // Navegar a la pantalla de detalle del producto
            val intent = ProductDetailActivity.newIntent(requireContext(), product.id)
            startActivity(intent)
        }
        
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.productsRecyclerView.adapter = productoAdapter
    }
    
    private fun observeViewModel() {
        viewModel.allProducts.observe(viewLifecycleOwner, Observer { products ->
            productoAdapter.submitList(products)
        })
        
        viewModel.cartItemCount.observe(viewLifecycleOwner, Observer { count ->
            // Actualizar UI del carrito si es necesario
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
