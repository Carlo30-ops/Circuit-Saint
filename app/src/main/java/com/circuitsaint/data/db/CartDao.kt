package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.CartItem
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Query("""
        SELECT cart_items.*, products.name, products.price, products.imageUrl, products.description
        FROM cart_items
        INNER JOIN products ON cart_items.productId = products.id
    """)
    fun getCartItemsWithProducts(): LiveData<List<CartItemWithProduct>>
    
    @Query("""
        SELECT cart_items.*, products.name, products.price, products.imageUrl, products.description
        FROM cart_items
        INNER JOIN products ON cart_items.productId = products.id
    """)
    fun getCartItemsWithProductsFlow(): Flow<List<CartItemWithProduct>>
    
    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: Long): CartItem?
    
    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    suspend fun getCartItemById(cartItemId: Long): CartItem?
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): LiveData<Int>
    
    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getTotalQuantity(): LiveData<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long
    
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
    
    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItemById(cartItemId: Long)
    
    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItemByProductId(productId: Long)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("SELECT SUM(products.price * cart_items.quantity) FROM cart_items INNER JOIN products ON cart_items.productId = products.id")
    fun getTotalPrice(): LiveData<Double?>
    
    @Query("""
        SELECT cart_items.*, products.name, products.price, products.imageUrl, products.description
        FROM cart_items
        INNER JOIN products ON cart_items.productId = products.id
    """)
    suspend fun getAllCartWithProducts(): List<CartItemWithProduct>
}

data class CartItemWithProduct(
    @Embedded val cartItem: CartItem,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val description: String
)

