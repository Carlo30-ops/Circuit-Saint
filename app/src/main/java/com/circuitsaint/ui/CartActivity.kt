package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.circuitsaint.R
import com.circuitsaint.databinding.ActivityCartBinding
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.domain.Result
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: StoreViewModel by viewModels()

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
            binding.totalPrice.text = getString(R.string.price_format, totalPrice)
        })
    }

    private fun finalizePurchase() {
        val totalPrice = viewModel.totalPrice.value ?: 0.0
        if (totalPrice > 0) {
            // Mostrar diálogo para datos del cliente
            showCheckoutDialog()
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showCheckoutDialog() {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)
        val input = android.widget.EditText(this)
        input.hint = "Nombre completo"
        val inputEmail = android.widget.EditText(this)
        inputEmail.hint = "Email"
        inputEmail.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val inputPhone = android.widget.EditText(this)
        inputPhone.hint = "Teléfono (opcional)"
        inputPhone.inputType = android.text.InputType.TYPE_CLASS_PHONE
        
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(input)
            addView(inputEmail)
            addView(inputPhone)
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Datos del Cliente")
            .setView(container)
            .setPositiveButton("Confirmar Compra") { _, _ ->
                val nombre = input.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val telefono = inputPhone.text.toString().trim().takeIf { it.isNotEmpty() }
                
                if (nombre.isEmpty() || email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Por favor ingresa nombre y email válidos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                viewModel.checkout(nombre, email, telefono)
                lifecycleScope.launch {
                    viewModel.checkoutState.collect { result ->
                        when (result) {
                            is Result.Success -> {
                                val order = result.data
                                Toast.makeText(
                                    this@CartActivity,
                                    "¡Compra exitosa! Pedido: ${order.numero_pedido}\nTotal: $${String.format("%.2f", order.total)}",
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.clearCheckoutState()
                                finish()
                            }
                            is Result.Error -> {
                                Toast.makeText(
                                    this@CartActivity,
                                    result.message ?: "Error al procesar la compra",
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.clearCheckoutState()
                            }
                            is Result.Loading -> {
                                // Mostrar loading si es necesario
                            }
                            null -> {
                                // Estado inicial
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
            binding.cartItemPrice.text = binding.root.context.getString(R.string.price_per_unit_format, price)
            binding.cartItemQuantity.text = quantity.toString()
            binding.cartItemSubtotal.text = binding.root.context.getString(R.string.price_format, subtotal)

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
