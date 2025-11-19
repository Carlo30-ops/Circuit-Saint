package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    
    @Query("SELECT * FROM pedidos ORDER BY created_at DESC")
    fun getAllOrders(): LiveData<List<Order>>
    
    @Query("SELECT * FROM pedidos ORDER BY created_at DESC")
    fun getAllOrdersFlow(): Flow<List<Order>>
    
    @Query("SELECT * FROM pedidos WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): Order?
    
    @Query("SELECT * FROM pedidos WHERE id = :orderId")
    fun getOrderByIdLiveData(orderId: Long): LiveData<Order?>
    
    @Query("SELECT * FROM pedidos WHERE numero_pedido = :numeroPedido")
    suspend fun getOrderByNumero(numeroPedido: String): Order?
    
    @Query("SELECT * FROM pedidos WHERE cliente_email = :email ORDER BY created_at DESC")
    fun getOrdersByEmail(email: String): LiveData<List<Order>>
    
    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY created_at DESC")
    fun getOrdersByEstado(estado: String): LiveData<List<Order>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Delete
    suspend fun deleteOrder(order: Order)
    
    @Query("UPDATE pedidos SET estado = :estado WHERE id = :orderId")
    suspend fun updateOrderEstado(orderId: Long, estado: String)
    
    @Query("SELECT COUNT(*) FROM pedidos")
    fun getOrderCount(): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    fun getOrderCountByEstado(estado: String): LiveData<Int>
    
    // Generar número de pedido único
    @Query("SELECT COUNT(*) FROM pedidos WHERE DATE(created_at/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodayOrderCount(): Int
}

