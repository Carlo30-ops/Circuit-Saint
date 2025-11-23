package com.circuitsaint.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import com.circuitsaint.data.db.CartItemWithProduct
import com.circuitsaint.data.model.Order
import com.circuitsaint.data.model.Product
import com.circuitsaint.data.repository.StoreRepository
import com.circuitsaint.domain.Result
import com.circuitsaint.domain.usecase.AddToCartUseCase
import com.circuitsaint.domain.usecase.CheckoutUseCase
import com.circuitsaint.domain.usecase.GetProductsPaginatedUseCase
import com.circuitsaint.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val getProductsPaginatedUseCase: GetProductsPaginatedUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val checkoutUseCase: CheckoutUseCase
) : ViewModel() {
    
    // ==================== Products ====================
    
    /**
     * Flow de productos paginados
     */
    fun getProductsPaginated(): Flow<PagingData<Product>> =
        getProductsPaginatedUseCase().cachedIn(viewModelScope)
    
    /**
     * Flow de búsqueda paginada
     */
    fun getProductsStream(
        query: String = "",
        category: String? = null
    ): Flow<PagingData<Product>> =
        searchProductsUseCase(query, category).cachedIn(viewModelScope)
    
    fun getProductByIdLiveData(productId: Long) = repository.getProductByIdLiveData(productId)
    
    /**
     * Obtiene un producto por ID como LiveData
     * Alias para getProductByIdLiveData para mantener compatibilidad
     */
    fun getProductById(productId: Long) = repository.getProductByIdLiveData(productId)
    
    // ==================== Cart ====================
    
    val cartItems = repository.getCartItemsWithProducts()
    val cartItemCount = repository.getCartItemCount()
    val totalPrice = repository.getTotalPrice()
    
    // ==================== Checkout State ====================
    
    private val _checkoutState = MutableStateFlow<Result<Order>?>(null)
    val checkoutState: StateFlow<Result<Order>?> = _checkoutState.asStateFlow()
    
    fun checkout(
        clienteNombre: String,
        clienteEmail: String,
        clienteTelefono: String? = null
    ) {
        viewModelScope.launch {
            _checkoutState.value = Result.Loading
            val result = checkoutUseCase(clienteNombre, clienteEmail, clienteTelefono)
            _checkoutState.value = result
        }
    }
    
    fun clearCheckoutState() {
        _checkoutState.value = null
    }
    
    // ==================== Cart Operations ====================
    
    fun addToCart(productId: Long, quantity: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = addToCartUseCase(productId, quantity)
            if (result is Result.Error) {
                Timber.e("Error agregando al carrito: ${result.message}")
            }
        }
    }
    
    fun updateCartItemQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCartItemQuantity(cartItemId, quantity)
        }
    }
    
    fun removeFromCart(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromCart(productId)
        }
    }
    
    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeCartItem(cartItemId)
        }
    }
    
    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCart()
        }
    }
    
    // ==================== Orders ====================
    
    val allOrders = repository.getAllOrders()
    
    // ==================== Contacts ====================
    
    val allContacts = repository.getAllContacts()
    val unreadContactCount = repository.getUnreadContactCount()
    
    private val _formSubmissionState = MutableStateFlow<Result<Unit>?>(null)
    val formSubmissionState: StateFlow<Result<Unit>?> = _formSubmissionState.asStateFlow()
    
    fun submitForm(name: String, email: String, message: String, telefono: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _formSubmissionState.value = Result.Loading
            try {
                val contact = com.circuitsaint.data.model.Contact(
                    nombre = name,
                    email = email,
                    telefono = telefono,
                    mensaje = message
                )
                repository.insertContact(contact)
                _formSubmissionState.value = Result.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error enviando formulario")
                _formSubmissionState.value = Result.Error(e, "Error al enviar el formulario: ${e.message}")
            }
        }
    }
    
    fun clearFormSubmissionState() {
        _formSubmissionState.value = null
    }
    
    // ==================== Categories ====================
    
    suspend fun getCategories(): List<String> = withContext(Dispatchers.IO) {
        repository.getCategories()
    }
    
    fun getProductsByCategory(categoria: String) = repository.getProductsByCategory(categoria)
}
