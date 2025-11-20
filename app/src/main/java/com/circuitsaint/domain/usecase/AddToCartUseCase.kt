package com.circuitsaint.domain.usecase

import com.circuitsaint.data.repository.StoreRepository
import com.circuitsaint.domain.Result
import com.circuitsaint.util.Config
import com.circuitsaint.util.isValidQuantity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    /**
     * Agrega producto al carrito con validación
     */
    suspend operator fun invoke(
        productId: Long,
        quantity: Int = 1
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Validar cantidad
                if (!quantity.isValidQuantity()) {
                    return@withContext Result.Error(
                        IllegalArgumentException("Cantidad inválida"),
                        "La cantidad debe estar entre ${Config.MIN_QUANTITY} y ${Config.MAX_QUANTITY}"
                    )
                }
                
                // Verificar que el producto existe y tiene stock
                val product = repository.getProductById(productId)
                if (product == null) {
                    return@withContext Result.Error(
                        IllegalArgumentException("Producto no encontrado"),
                        "El producto solicitado no existe"
                    )
                }
                
                if (product.stock < quantity) {
                    return@withContext Result.Error(
                        IllegalStateException("Stock insuficiente"),
                        "No hay suficiente stock. Disponible: ${product.stock}"
                    )
                }
                
                // Agregar al carrito
                repository.addToCart(productId, quantity)
                Timber.d("Producto $productId agregado al carrito: cantidad $quantity")
                Result.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error agregando producto al carrito")
                Result.Error(e, "Error al agregar producto al carrito: ${e.message}")
            }
        }
    }
}

