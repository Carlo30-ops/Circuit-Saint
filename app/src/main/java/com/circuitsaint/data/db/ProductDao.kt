package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    fun getAllProducts(): LiveData<List<Product>>
    
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    fun getAllProductsFlow(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE activo = 1 AND categoria = :categoria ORDER BY created_at DESC")
    fun getProductsByCategory(categoria: String): LiveData<List<Product>>
    
    @Query("SELECT DISTINCT categoria FROM products WHERE activo = 1 ORDER BY categoria")
    suspend fun getCategories(): List<String>
    
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

