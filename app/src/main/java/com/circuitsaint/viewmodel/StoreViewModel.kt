package com.circuitsaint.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.data.model.Product
import com.circuitsaint.data.repository.StoreRepository
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: StoreRepository
    
    val allProducts: LiveData<List<Product>>
    val cartItems: LiveData<List<CartItemWithProduct>>
    val cartItemCount: LiveData<Int>
    val totalPrice: LiveData<Double?>
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = StoreRepository(database)
        
        allProducts = repository.getAllProducts()
        cartItems = repository.getCartItemsWithProducts()
        cartItemCount = repository.getCartItemCount()
        totalPrice = repository.getTotalPrice()
    }
    
    fun getProductById(productId: Long): LiveData<Product?> {
        return repository.getProductByIdLiveData(productId)
    }
    
    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }
    
    fun insertProducts(products: List<Product>) {
        viewModelScope.launch {
            repository.insertProducts(products)
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
    
    fun addToCart(productId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, quantity)
        }
    }
    
    fun updateCartItemQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartItemId, quantity)
        }
    }
    
    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }
    
    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}

