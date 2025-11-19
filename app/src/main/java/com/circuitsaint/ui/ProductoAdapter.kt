package com.circuitsaint.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.circuitsaint.data.model.Product
import com.circuitsaint.databinding.ItemProductoBinding

class ProductoAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductoAdapter.ProductoViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, onItemClick: (Product) -> Unit) {
            binding.productName.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "$${String.format("%.2f", product.price)}"

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

