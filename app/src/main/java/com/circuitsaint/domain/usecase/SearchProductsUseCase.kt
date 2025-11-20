package com.circuitsaint.domain.usecase

import androidx.paging.PagingData
import com.circuitsaint.data.repository.StoreRepository
import com.circuitsaint.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    /**
     * Busca productos con paginación
     */
    operator fun invoke(query: String): Flow<PagingData<com.circuitsaint.data.model.Product>> {
        if (query.isBlank()) {
            return repository.getProductsPaginated()
        }
        
        return repository.searchProductsPaginated(query)
            .catch { exception ->
                Timber.e(exception, "Error buscando productos: $query")
                throw exception
            }
    }
}


