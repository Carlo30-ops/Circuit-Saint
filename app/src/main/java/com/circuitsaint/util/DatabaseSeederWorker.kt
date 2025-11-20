package com.circuitsaint.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.model.Product
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DatabaseSeederWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val productDao = database.productDao()
            
            // Verificar si ya hay productos
            val existingProducts = productDao.getAllProductsSuspend()
            if (existingProducts.isNotEmpty()) {
                Timber.d("Base de datos ya tiene ${existingProducts.size} productos. Saltando seeding.")
                return Result.success()
            }
            
            // Insertar productos
            val products = getSeedProducts()
            productDao.insertProducts(products)
            
            Timber.d("Seeding completado: ${products.size} productos insertados")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error en DatabaseSeederWorker")
            Result.retry()
        }
    }
    
    private fun getSeedProducts(): List<Product> {
        // Reutilizar la lógica del DatabaseSeeder original
        return DatabaseSeeder.getSeedProducts()
    }
}


