package com.circuitsaint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.circuitsaint.R
import com.circuitsaint.databinding.FragmentProductDetailBinding
import com.circuitsaint.util.FormatUtils
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoreViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    private var currentQuantity = 1
    private var currentProduct: com.circuitsaint.data.model.Product? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeProduct()
    }

    private fun setupListeners() {
        binding.btnIncrease.setOnClickListener {
            val maxStock = currentProduct?.stock ?: 0
            if (currentQuantity < maxStock) {
                currentQuantity++
                updateQuantity()
            }
        }

        binding.btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                updateQuantity()
            }
        }

        binding.addToCartButton.setOnClickListener {
            val product = currentProduct ?: return@setOnClickListener
            if (product.stock <= 0) {
                Toast.makeText(requireContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addToCart(product.id, currentQuantity)
            Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyNow.setOnClickListener {
            val product = currentProduct ?: return@setOnClickListener
            if (product.stock <= 0) {
                Toast.makeText(requireContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addToCart(product.id, currentQuantity)
            findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailToCart())
        }
    }

    private fun observeProduct() {
        viewModel.getProductById(args.productId).observe(viewLifecycleOwner) { product ->
            if (product == null) {
                Toast.makeText(requireContext(), R.string.product_not_found, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }
            currentProduct = product
            binding.productName.text = product.name
            binding.categoryBadge.text = "★ ${product.categoria} ★"
            binding.productPrice.text = FormatUtils.formatPrice(product.price)
            binding.productDescription.text = product.description
            binding.productStock.text = getString(R.string.stock_format, product.stock)
            binding.featuresText.text = getString(R.string.product_features_default)

            Glide.with(this)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .into(binding.productImageDetail)

            currentQuantity = 1
            updateQuantity()
        }
    }

    private fun updateQuantity() {
        binding.quantityText.text = currentQuantity.toString()
        val maxStock = currentProduct?.stock ?: 0
        binding.btnIncrease.isEnabled = currentQuantity < maxStock
        binding.btnDecrease.isEnabled = currentQuantity > 1
        binding.addToCartButton.isEnabled = maxStock > 0
        binding.btnBuyNow.isEnabled = maxStock > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

