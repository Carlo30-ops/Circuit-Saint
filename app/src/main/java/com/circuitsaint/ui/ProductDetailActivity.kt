package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.circuitsaint.R
import com.circuitsaint.databinding.ActivityProductDetailBinding
import com.circuitsaint.viewmodel.StoreViewModel
import com.circuitsaint.viewmodel.StoreViewModelFactory

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory(application)
    }

    private var productId: Long = -1
    private var currentQuantity: Int = 1

    companion object {
        private const val EXTRA_PRODUCT_ID = "extra_product_id"

        fun newIntent(context: Context, productId: Long): Intent {
            return Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1)
        if (productId == -1L) {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnIncrease.setOnClickListener {
            val product = viewModel.getProductById(productId).value
            val maxStock = product?.stock ?: 0
            if (currentQuantity < maxStock) {
                currentQuantity++
                updateQuantityDisplay()
                updateButtonsState(maxStock)
            }
        }

        binding.btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                updateQuantityDisplay()
                val product = viewModel.getProductById(productId).value
                updateButtonsState(product?.stock ?: 0)
            }
        }

        binding.addToCartButton.setOnClickListener {
            viewModel.addToCart(productId, currentQuantity)
            Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateQuantityDisplay() {
        binding.quantityText.text = currentQuantity.toString()
    }

    private fun observeViewModel() {
        viewModel.getProductById(productId).observe(this, Observer { product ->
            product?.let {
                binding.productName.text = it.name
                binding.productDescription.text = it.description
                binding.productPrice.text = getString(R.string.price_format, it.price)
                binding.productStock.text = getString(R.string.stock_format, it.stock)

                updateButtonsState(it.stock)

                // Actualizar título de la actividad
                supportActionBar?.title = it.name
            } ?: run {
                Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun updateButtonsState(stock: Int) {
        val hasStock = stock > 0
        binding.addToCartButton.isEnabled = hasStock && currentQuantity > 0
        binding.btnIncrease.isEnabled = hasStock && currentQuantity < stock
        binding.btnDecrease.isEnabled = currentQuantity > 1
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
