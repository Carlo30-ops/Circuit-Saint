package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.circuitsaint.databinding.ActivityCartBinding
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.viewmodel.StoreViewModel
import com.circuitsaint.viewmodel.StoreViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory(application)
    }

    private lateinit var cartAdapter: CartAdapter

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Carrito de Compras"

        setupRecyclerView()
        setupViews()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityIncrease = { cartItemId, currentQuantity ->
                viewModel.updateCartItemQuantity(cartItemId, currentQuantity + 1)
            },
            onQuantityDecrease = { cartItemId, currentQuantity ->
                if (currentQuantity > 1) {
                    viewModel.updateCartItemQuantity(cartItemId, currentQuantity - 1)
                }
            },
            onRemoveItem = { cartItemId ->
                viewModel.removeCartItem(cartItemId)
            }
        )

        binding.cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartItemsRecyclerView.adapter = cartAdapter
    }

    private fun setupViews() {
        binding.btnFinalizePurchase.setOnClickListener {
            finalizePurchase()
        }
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(this, Observer { cartItems ->
            if (cartItems.isEmpty()) {
                binding.emptyCartMessage.visibility = android.view.View.VISIBLE
                binding.cartItemsRecyclerView.visibility = android.view.View.GONE
                binding.btnFinalizePurchase.isEnabled = false
            } else {
                binding.emptyCartMessage.visibility = android.view.View.GONE
                binding.cartItemsRecyclerView.visibility = android.view.View.VISIBLE
                binding.btnFinalizePurchase.isEnabled = true
            }
            cartAdapter.submitList(cartItems)
        })

        viewModel.totalPrice.observe(this, Observer { total ->
            val totalPrice = total ?: 0.0
            binding.totalPrice.text = "$${String.format("%.2f", totalPrice)}"
        })
    }

    private fun finalizePurchase() {
        val totalPrice = viewModel.totalPrice.value ?: 0.0
        if (totalPrice > 0) {
            viewModel.checkout()
            viewModel.checkoutState.observe(this) { success ->
                if (success) {
                    Toast.makeText(
                        this,
                        "Compra finalizada. Total: $${String.format("%.2f", totalPrice)}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error: No hay suficiente stock para algunos productos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
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

class CartAdapter(
    private val onQuantityIncrease: (Long, Int) -> Unit,
    private val onQuantityDecrease: (Long, Int) -> Unit,
    private val onRemoveItem: (Long) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(
    CartDiffCallback()
) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CartViewHolder {
        val binding = com.circuitsaint.databinding.ItemCartBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: com.circuitsaint.databinding.ItemCartBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItemWithProduct: CartItemWithProduct) {
            val cartItem = cartItemWithProduct.cartItem
            val quantity = cartItem.quantity
            val price = cartItemWithProduct.price
            val subtotal = price * quantity

            binding.cartItemName.text = cartItemWithProduct.name
            binding.cartItemPrice.text = "$${String.format("%.2f", price)} c/u"
            binding.cartItemQuantity.text = quantity.toString()
            binding.cartItemSubtotal.text = "$${String.format("%.2f", subtotal)}"

            binding.btnIncreaseQuantity.setOnClickListener {
                onQuantityIncrease(cartItem.id, quantity)
            }

            binding.btnDecreaseQuantity.setOnClickListener {
                onQuantityDecrease(cartItem.id, quantity)
            }

            binding.btnRemoveItem.setOnClickListener {
                onRemoveItem(cartItem.id)
            }
        }
    }

    private class CartDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<CartItemWithProduct>() {
        override fun areItemsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem.cartItem.id == newItem.cartItem.id
        }

        override fun areContentsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem == newItem
        }
    }
}

