package com.circuitsaint.domain.usecase

import com.circuitsaint.data.repository.StoreRepository
import com.circuitsaint.domain.Result
import com.circuitsaint.util.validateCheckoutData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CheckoutUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    /**
     * Procesa el checkout de manera atómica
     * Valida datos de entrada y maneja errores explícitamente
     */
    suspend operator fun invoke(
        clienteNombre: String,
        clienteEmail: String,
        clienteTelefono: String? = null
    ): Result<com.circuitsaint.data.model.Order> {
        return withContext(Dispatchers.IO) {
            try {
                // Validar datos de entrada
                val validationResult = validateCheckoutData(
                    clienteNombre,
                    clienteEmail,
                    clienteTelefono
                )
                
                if (validationResult is Result.Error) {
                    return@withContext validationResult
                }
                
                // Ejecutar checkout (ya es transaccional en el repository)
                val order = repository.checkout(
                    clienteNombre,
                    clienteEmail,
                    clienteTelefono
                )
                
                if (order != null) {
                    Timber.d("Checkout exitoso: ${order.numero_pedido}")
                    Result.Success(order)
                } else {
                    Timber.w("Checkout falló: stock insuficiente o carrito vacío")
                    Result.Error(
                        IllegalStateException("No se pudo procesar el checkout"),
                        "No hay suficiente stock para algunos productos o el carrito está vacío"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error en checkout")
                Result.Error(e, "Error al procesar la compra: ${e.message}")
            }
        }
    }
}


