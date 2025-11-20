package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    /**
     * PagingSource para paginación eficiente
     */
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    fun getAllProductsPaged(): PagingSource<Int, Product>
    
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    suspend fun getAllProductsSuspend(): List<Product>

    /**
     * Búsqueda mejorada - usa índice si está disponible
     * Nota: Para mejor rendimiento, considerar FTS (Full Text Search) en el futuro
     */
    @Query("SELECT * FROM products WHERE activo = 1 AND name LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchProducts(query: String): List<Product>
    
    /**
     * PagingSource para búsqueda paginada
     */
    @Query("SELECT * FROM products WHERE activo = 1 AND name LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchProductsPaged(query: String): PagingSource<Int, Product>
    
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    fun getAllProductsFlow(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE activo = 1 AND categoria = :categoria ORDER BY created_at DESC")
    fun getProductsByCategory(categoria: String): LiveData<List<Product>>
    
    @Query("SELECT * FROM products WHERE activo = 1 AND categoria = :categoria ORDER BY created_at DESC")
    suspend fun getProductsByCategorySuspend(categoria: String): List<Product>
    
    @Query("SELECT DISTINCT categoria FROM products WHERE activo = 1 ORDER BY categoria")
    suspend fun getCategories(): List<String>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product?
    
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdLiveData(productId: Long): LiveData<Product?>
    
    // Método legacy para compatibilidad (deprecated, usar Paging3)
    @Query("SELECT * FROM products WHERE activo = 1 ORDER BY created_at DESC")
    fun getAllProducts(): LiveData<List<Product>>
    
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
    
    /**
     * Actualiza el stock de manera condicional para evitar race conditions.
     * Solo actualiza si hay suficiente stock disponible.
     * Retorna el número de filas afectadas (1 si exitoso, 0 si falló).
     */
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity")
    suspend fun decrementStockConditionally(productId: Long, quantity: Int): Int
}

