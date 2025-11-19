package com.circuitsaint.data.repository

import androidx.lifecycle.LiveData
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.db.CartDao
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.data.db.ContactDao
import com.circuitsaint.data.db.OrderDao
import com.circuitsaint.data.db.OrderItemDao
import com.circuitsaint.data.db.ProductDao
import com.circuitsaint.data.model.CartItem
import com.circuitsaint.data.model.Contact
import com.circuitsaint.data.model.Order
import com.circuitsaint.data.model.OrderItem
import com.circuitsaint.data.model.Product
import kotlinx.coroutines.flow.Flow

class StoreRepository(private val database: AppDatabase) {
    
    private val productDao: ProductDao = database.productDao()
    private val cartDao: CartDao = database.cartDao()
    private val orderDao: OrderDao = database.orderDao()
    private val orderItemDao: OrderItemDao = database.orderItemDao()
    private val contactDao: ContactDao = database.contactDao()
    
    // Product operations
    fun getAllProducts(): LiveData<List<Product>> = productDao.getAllProducts()
    
    suspend fun getAllProductsSuspend(): List<Product> = productDao.getAllProductsSuspend()

    suspend fun searchProducts(query: String): List<Product> = productDao.searchProducts(query)
    
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
    
    suspend fun checkout(
        clienteNombre: String,
        clienteEmail: String,
        clienteTelefono: String? = null
    ): Order? {
        val items = cartDao.getAllCartWithProducts()
        if (items.isEmpty()) return null
        
        // Verificar stock
        for (item in items) {
            val product = productDao.getProductById(item.cartItem.productId)
            if (product == null || product.stock < item.cartItem.quantity) {
                return null // Stock insuficiente
            }
        }
        
        // Calcular total
        val total = items.sumOf { it.price * it.cartItem.quantity }
        
        // Generar número de pedido
        val todayCount = orderDao.getTodayOrderCount()
        val dateStr = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(java.util.Date())
        val numeroPedido = "CS-$dateStr-${String.format("%04d", todayCount + 1)}"
        
        // Crear pedido
        val order = Order(
            numero_pedido = numeroPedido,
            cliente_nombre = clienteNombre,
            cliente_email = clienteEmail,
            cliente_telefono = clienteTelefono,
            total = total,
            estado = "pendiente"
        )
        val orderId = orderDao.insertOrder(order)
        
        // Crear items del pedido y actualizar stock
        val orderItems = items.map { item ->
            val product = productDao.getProductById(item.cartItem.productId)!!
            val newStock = product.stock - item.cartItem.quantity
            productDao.updateProductStock(product.id, newStock)
            
            OrderItem(
                pedido_id = orderId,
                producto_id = item.cartItem.productId,
                cantidad = item.cartItem.quantity,
                precio_unitario = item.price,
                subtotal = item.price * item.cartItem.quantity
            )
        }
        orderItemDao.insertOrderItems(orderItems)
        
        // Limpiar carrito
        cartDao.clearCart()
        
        return order.copy(id = orderId)
    }
    
    // Order operations
    fun getAllOrders(): LiveData<List<Order>> = orderDao.getAllOrders()
    
    suspend fun getOrderById(orderId: Long): Order? = orderDao.getOrderById(orderId)
    
    fun getOrderByIdLiveData(orderId: Long): LiveData<Order?> = orderDao.getOrderByIdLiveData(orderId)
    
    fun getOrdersByEmail(email: String): LiveData<List<Order>> = orderDao.getOrdersByEmail(email)
    
    suspend fun updateOrderEstado(orderId: Long, estado: String) = 
        orderDao.updateOrderEstado(orderId, estado)
    
    fun getOrderItemsWithProducts(orderId: Long): LiveData<List<com.circuitsaint.data.db.OrderItemWithProduct>> =
        orderItemDao.getOrderItemsWithProducts(orderId)
    
    // Contact operations
    fun getAllContacts(): LiveData<List<Contact>> = contactDao.getAllContacts()
    
    suspend fun insertContact(contact: Contact): Long = contactDao.insertContact(contact)
    
    suspend fun updateContactLeido(contactId: Long, leido: Boolean) = 
        contactDao.updateContactLeido(contactId, leido)
    
    fun getUnreadContactCount(): LiveData<Int> = contactDao.getUnreadContactCount()
    
    // Product category operations
    suspend fun getCategories(): List<String> = productDao.getCategories()
    
    fun getProductsByCategory(categoria: String): LiveData<List<Product>> = 
        productDao.getProductsByCategory(categoria)
        
    suspend fun getProductsByCategorySuspend(categoria: String): List<Product> = 
        productDao.getProductsByCategorySuspend(categoria)
}
