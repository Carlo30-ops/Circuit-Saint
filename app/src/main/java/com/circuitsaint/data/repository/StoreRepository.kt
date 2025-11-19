package com.circuitsaint.data.repository

import androidx.lifecycle.LiveData
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.db.CartDao
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.data.db.ProductDao
import com.circuitsaint.data.model.CartItem
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

class StoreRepository(private val database: AppDatabase) {
    
    private val productDao: ProductDao = database.productDao()
    private val cartDao: CartDao = database.cartDao()
    
    // Product operations
    fun getAllProducts(): LiveData<List<Product>> = productDao.getAllProducts()
    
    fun getAllProductsFlow(): Flow<List<Product>> = productDao.getAllProductsFlow()
    
    suspend fun getProductById(productId: Long): Product? = productDao.getProductById(productId)
    
    fun getProductByIdLiveData(productId: Long): LiveData<Product?> = 
        productDao.getProductByIdLiveData(productId)
    
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    
    suspend fun insertProducts(products: List<Product>) = productDao.insertProducts(products)
    
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    
    suspend fun deleteProductById(productId: Long) = productDao.deleteProductById(productId)
    
    // Cart operations
    fun getCartItemsWithProducts(): LiveData<List<CartItemWithProduct>> = 
        cartDao.getCartItemsWithProducts()
    
    fun getCartItemsWithProductsFlow(): Flow<List<CartItemWithProduct>> = 
        cartDao.getCartItemsWithProductsFlow()
    
    suspend fun getCartItemByProductId(productId: Long): CartItem? = 
        cartDao.getCartItemByProductId(productId)
    
    fun getCartItemCount(): LiveData<Int> = cartDao.getCartItemCount()
    
    fun getTotalQuantity(): LiveData<Int> = cartDao.getTotalQuantity()
    
    fun getTotalPrice(): LiveData<Double?> = cartDao.getTotalPrice()
    
    suspend fun addToCart(productId: Long, quantity: Int = 1) {
        val existingItem = cartDao.getCartItemByProductId(productId)
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            cartDao.updateCartItem(updatedItem)
        } else {
            cartDao.insertCartItem(CartItem(productId = productId, quantity = quantity))
        }
    }
    
    suspend fun updateCartItemQuantity(cartItemId: Long, quantity: Int) {
        val cartItem = cartDao.getCartItemById(cartItemId)
        if (cartItem != null) {
            cartDao.updateCartItem(cartItem.copy(quantity = quantity))
        }
    }
    
    suspend fun getCartItemById(cartItemId: Long): CartItem? = cartDao.getCartItemById(cartItemId)
    
    suspend fun removeFromCart(productId: Long) = cartDao.deleteCartItemByProductId(productId)
    
    suspend fun removeCartItem(cartItemId: Long) = cartDao.deleteCartItemById(cartItemId)
    
    suspend fun clearCart() = cartDao.clearCart()
}

