package com.circuitsaint.ui

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.circuitsaint.R
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.databinding.FragmentCartBinding
import com.circuitsaint.databinding.ItemCartBinding
import com.circuitsaint.domain.Result
import com.circuitsaint.util.FormatUtils
import com.circuitsaint.viewmodel.StoreViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoreViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private var currentTotal: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        binding.btnFinalizePurchase.setOnClickListener { attemptCheckout() }
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

        binding.cartItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            val isEmpty = cartItems.isEmpty()
            binding.emptyCartMessage.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.cartItemsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.btnFinalizePurchase.isEnabled = !isEmpty
            cartAdapter.submitList(cartItems)
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            currentTotal = total ?: 0.0
            binding.totalPrice.text = FormatUtils.formatPrice(currentTotal)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.checkoutState.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val order = result.data
                            Toast.makeText(
                                requireContext(),
                                getString(
                                    R.string.total_purchase_format,
                                    FormatUtils.formatPrice(order.total)
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.clearCheckoutState()
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                requireContext(),
                                result.message ?: getString(R.string.contact_error),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.clearCheckoutState()
                        }
                        is Result.Loading -> {
                            binding.btnFinalizePurchase.isEnabled = false
                        }
                        null -> binding.btnFinalizePurchase.isEnabled = cartAdapter.itemCount > 0
                    }
                }
            }
        }
    }

    private fun attemptCheckout() {
        if (currentTotal <= 0.0) {
            Toast.makeText(requireContext(), R.string.cart_empty_message, Toast.LENGTH_SHORT).show()
            return
        }
        showCheckoutDialog()
    }

    private fun showCheckoutDialog() {
        val inputName = EditText(requireContext()).apply {
            hint = getString(R.string.enter_name_hint)
        }
        val inputEmail = EditText(requireContext()).apply {
            hint = getString(R.string.enter_email_hint)
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val inputPhone = EditText(requireContext()).apply {
            hint = getString(R.string.enter_phone_hint)
            inputType = InputType.TYPE_CLASS_PHONE
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(inputName)
            addView(inputEmail)
            addView(inputPhone)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.checkout_dialog_title))
            .setView(container)
            .setPositiveButton(getString(R.string.checkout_positive)) { _, _ ->
                val name = inputName.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val phone = inputPhone.text.toString().trim().takeIf { it.isNotEmpty() }

                if (name.isEmpty() || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(requireContext(), R.string.contact_error, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.checkout(name, email, phone)
            }
            .setNegativeButton(getString(R.string.checkout_negative), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class CartAdapter(
    private val onQuantityIncrease: (Long, Int) -> Unit,
    private val onQuantityDecrease: (Long, Int) -> Unit,
    private val onRemoveItem: (Long) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<CartItemWithProduct>() {
        override fun areItemsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean = oldItem.cartItem.id == newItem.cartItem.id

        override fun areContentsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItemWithProduct) {
            val cartItem = item.cartItem
            val quantity = cartItem.quantity
            val price = item.price
            val subtotal = price * quantity

            binding.cartItemName.text = item.name
            binding.cartItemPrice.text = binding.root.context.getString(
                R.string.price_per_unit_text,
                FormatUtils.formatPrice(price)
            )
            binding.cartItemQuantity.text = quantity.toString()
            binding.cartItemSubtotal.text = FormatUtils.formatPrice(subtotal)

            Glide.with(binding.cartItemImage)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .into(binding.cartItemImage)

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
}

