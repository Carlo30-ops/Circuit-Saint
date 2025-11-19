package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.OrderItem
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    
    @Query("SELECT * FROM pedido_items WHERE pedido_id = :orderId")
    suspend fun getOrderItemsByOrderId(orderId: Long): List<OrderItem>
    
    @Query("SELECT * FROM pedido_items WHERE pedido_id = :orderId")
    fun getOrderItemsByOrderIdLiveData(orderId: Long): LiveData<List<OrderItem>>
    
    @Query("""
        SELECT pedido_items.*, products.name, products.imageUrl
        FROM pedido_items
        INNER JOIN products ON pedido_items.producto_id = products.id
        WHERE pedido_items.pedido_id = :orderId
    """)
    fun getOrderItemsWithProducts(orderId: Long): LiveData<List<OrderItemWithProduct>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
    
    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)
    
    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)
    
    @Query("DELETE FROM pedido_items WHERE pedido_id = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: Long)
}

data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItem,
    val name: String,
    val imageUrl: String?
)

