package com.circuitsaint.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.data.model.Order
import com.circuitsaint.data.model.Product
import com.circuitsaint.data.repository.StoreRepository
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: StoreRepository
    
    val allProducts: LiveData<List<Product>>
    val cartItems: LiveData<List<CartItemWithProduct>>
    val cartItemCount: LiveData<Int>
    val totalPrice: LiveData<Double?>

    private val _checkoutState = MutableLiveData<Order?>()
    val checkoutState: LiveData<Order?> = _checkoutState

    val allOrders: LiveData<List<Order>>

    val allContacts: LiveData<List<com.circuitsaint.data.model.Contact>>
    val unreadContactCount: LiveData<Int>

    private val _formSubmissionLiveData = MutableLiveData<Triple<String, String, String>>()
    val formSubmissionLiveData: LiveData<Triple<String, String, String>> = _formSubmissionLiveData

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StoreRepository(database)

        allProducts = repository.getAllProducts()
        cartItems = repository.getCartItemsWithProducts()
        cartItemCount = repository.getCartItemCount()
        totalPrice = repository.getTotalPrice()
        allOrders = repository.getAllOrders()
        allContacts = repository.getAllContacts()
        unreadContactCount = repository.getUnreadContactCount()
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

    fun checkout(
        clienteNombre: String,
        clienteEmail: String,
        clienteTelefono: String? = null
    ) {
        viewModelScope.launch {
            val order = repository.checkout(clienteNombre, clienteEmail, clienteTelefono)
            _checkoutState.postValue(order)
        }
    }

    fun submitForm(name: String, email: String, message: String, telefono: String? = null) {
        viewModelScope.launch {
            val contact = com.circuitsaint.data.model.Contact(
                nombre = name,
                email = email,
                telefono = telefono,
                mensaje = message
            )
            repository.insertContact(contact)
            _formSubmissionLiveData.postValue(Triple(name, email, message))
        }
    }

    fun getProductsByCategory(categoria: String): LiveData<List<Product>> {
        return repository.getProductsByCategory(categoria)
    }

    suspend fun getCategories(): List<String> {
        return repository.getCategories()
    }
}

