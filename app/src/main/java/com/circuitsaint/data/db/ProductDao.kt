package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products")
    fun getAllProducts(): LiveData<List<Product>>
    
    @Query("SELECT * FROM products")
    fun getAllProductsFlow(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product?
    
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdLiveData(productId: Long): LiveData<Product?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Long)
    
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
    
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateProductStock(productId: Long, newStock: Int)
}

