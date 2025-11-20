package com.circuitsaint.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.circuitsaint.R
import com.circuitsaint.data.model.Product
import com.circuitsaint.databinding.ItemProductoBinding

class ProductoAdapter(
    private val onItemClick: (Product) -> Unit
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
            holder.bind(it, onItemClick)
        }
    }

    class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, onItemClick: (Product) -> Unit) {
            binding.productName.text = product.name
            // El layout actual no muestra descripción; centramos la info en nombre, categoría y precio
            binding.productPrice.text = String.format("$%.0f", product.price)

            // Cargar imagen con Glide
            Glide.with(binding.productImage.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .centerCrop()
                .into(binding.productImage)

            binding.addToCartButton.setOnClickListener {
                onItemClick(product)
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
