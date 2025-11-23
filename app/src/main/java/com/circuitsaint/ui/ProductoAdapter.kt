package com.circuitsaint.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.circuitsaint.R
import com.circuitsaint.data.model.Product
import com.circuitsaint.databinding.ItemProductoBinding
import com.circuitsaint.util.FormatUtils

class ProductoAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : PagingDataAdapter<Product, ProductoAdapter.ProductoViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val product = getItem(position)
        product?.let {
            holder.bind(it, onItemClick, onAddToCart)
        }
    }

    class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            product: Product,
            onItemClick: (Product) -> Unit,
            onAddToCart: (Product) -> Unit
        ) {
            binding.productName.text = product.name
            binding.productPrice.text = FormatUtils.formatPrice(product.price)
            binding.categoryBadge.text = "★ ${product.categoria} ★"
            binding.lowStockBadge.visibility = if (product.stock < 5) View.VISIBLE else View.GONE

            Glide.with(binding.productImage.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .centerCrop()
                .into(binding.productImage)

            binding.addToCartButton.isEnabled = product.stock > 0
            binding.addToCartButton.text =
                if (product.stock > 0) binding.root.context.getString(R.string.add_to_cart_button)
                else binding.root.context.getString(R.string.sold_out_button)

            binding.addToCartButton.setOnClickListener {
                if (product.stock > 0) {
                    onAddToCart(product)
                }
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
