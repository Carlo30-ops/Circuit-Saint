package com.circuitsaint.domain.usecase

import androidx.paging.PagingData
import com.circuitsaint.data.repository.StoreRepository
import com.circuitsaint.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GetProductsPaginatedUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    /**
     * Obtiene productos paginados usando Paging3
     * Retorna un Flow de PagingData para manejo eficiente de listas grandes
     */
    operator fun invoke(): Flow<PagingData<com.circuitsaint.data.model.Product>> {
        return repository.getProductsPaginated()
            .catch { exception ->
                Timber.e(exception, "Error obteniendo productos paginados")
                throw exception
            }
    }
}


