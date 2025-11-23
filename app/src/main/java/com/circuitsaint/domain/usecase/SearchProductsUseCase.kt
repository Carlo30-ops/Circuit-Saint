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
    operator fun invoke(
        query: String,
        category: String? = null
    ): Flow<PagingData<com.circuitsaint.data.model.Product>> {
        val sanitizedQuery = query.trim()
        val sanitizedCategory = category?.takeUnless { it.isBlank() }

        val baseFlow = if (sanitizedQuery.isEmpty() && sanitizedCategory == null) {
            repository.getProductsPaginated()
        } else {
            repository.searchProductsPaginated(sanitizedQuery, sanitizedCategory)
        }

        return baseFlow.catch { exception ->
            Timber.e(exception, "Error buscando productos: $sanitizedQuery categoría: $sanitizedCategory")
            throw exception
        }
    }
}


