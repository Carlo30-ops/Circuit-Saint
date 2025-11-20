package com.circuitsaint.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.circuitsaint.data.model.Product
import timber.log.Timber

object DatabaseSeeder {

    /**
     * Inicia el seeding de la base de datos usando WorkManager
     */
    fun startSeeding(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .build()

            val seedWorkRequest = OneTimeWorkRequestBuilder<DatabaseSeederWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "database_seeder_work",
                    ExistingWorkPolicy.KEEP,
                    seedWorkRequest
                )

            Timber.d("DatabaseSeeder: WorkManager enqueued para seeding")
        } catch (e: Exception) {
            Timber.e(e, "Error iniciando WorkManager para seeding")
        }
    }

    /**
     * Productos base alineados con la PWA Circuit Saint (incluye las imágenes oficiales de Unsplash)
     */
    fun getSeedProducts(): List<Product> = listOf(
        Product(
            name = "Audífonos Bluetooth Premium",
            description = "Audífonos inalámbricos con cancelación de ruido activa, batería de larga duración (hasta 30 horas), sonido premium y micrófono integrado. Perfectos para trabajo y entretenimiento.",
            price = 120000.0,
            stock = 15,
            categoria = "Audio",
            imageUrl = "https://images.unsplash.com/photo-1600086827875-a63b01f1335c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxibHVldG9vdGglMjBoZWFkcGhvbmVzJTIwd2hpdGV8ZW58MXx8fHwxNzYzNTIxMDc3fDA&ixlib=rb-4.1.0&q=80&w=1080"
        ),
        Product(
            name = "Mouse Inalámbrico Ergonómico",
            description = "Mouse inalámbrico de alta precisión con diseño ergonómico. Sensor óptico de 1600 DPI, batería recargable y conexión Bluetooth. Ideal para trabajo prolongado.",
            price = 80000.0,
            stock = 25,
            categoria = "Accesorios",
            imageUrl = "https://images.unsplash.com/photo-1760482280819-3212f185d50d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx3aXJlbGVzcyUyMG1vdXNlJTIwY29tcHV0ZXJ8ZW58MXx8fHwxNzYzNTIxMDc3fDA&ixlib=rb-4.1.0&q=80&w=1080"
        ),
        Product(
            name = "Teclado Mecánico Gamer RGB",
            description = "Teclado mecánico con switches red, retroiluminación RGB personalizable, teclas anti-ghosting y diseño compacto. Perfecto para gaming y programación.",
            price = 150000.0,
            stock = 12,
            categoria = "Gaming",
            imageUrl = "https://images.unsplash.com/photo-1645802106095-765b7e86f5bb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnYW1pbmclMjBrZXlib2FyZCUyMHJnYnxlbnwxfHx8fDE3NjM0NzI0MTJ8MA&ixlib=rb-4.1.0&q=80&w=1080"
        ),
        Product(
            name = "Laptop Ultrabook 14\"",
            description = "Laptop ultraligera con procesador Intel Core i7, 16GB RAM, SSD 512GB, pantalla Full HD 14 pulgadas. Perfecta para productividad y movilidad.",
            price = 2500000.0,
            stock = 8,
            categoria = "Computadoras",
            imageUrl = "https://images.unsplash.com/photo-1759668358660-0d06064f0f84?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYXB0b3AlMjBjb21wdXRlciUyMG1vZGVybnxlbnwxfHx8fDE3NjM1MjEwNzh8MA&ixlib=rb-4.1.0&q=80&w=1080"
        ),
        Product(
            name = "Smartphone 5G 128GB",
            description = "Smartphone con conectividad 5G, cámara triple de 48MP, pantalla AMOLED 6.5 pulgadas, batería de 5000mAh y carga rápida. Sistema operativo Android.",
            price = 1800000.0,
            stock = 20,
            categoria = "Smartphones",
            imageUrl = "https://images.unsplash.com/photo-1741061963569-9d0ef54d10d2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzbWFydHBob25lJTIwbW9iaWxlJTIwcGhvbmV8ZW58MXx8fHwxNjM0ODYyODR8MA&ixlib=rb-4.1.0&q=80&w=1080"
        ),
        Product(
            name = "Smartwatch Fitness Tracker",
            description = "Reloj inteligente con monitor de frecuencia cardíaca, GPS integrado, resistente al agua, seguimiento de ejercicios y notificaciones inteligentes. Batería de 7 días.",
            price = 450000.0,
            stock = 18,
            categoria = "Wearables",
            imageUrl = "https://images.unsplash.com/photo-1665860455418-017fa50d29bc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzbWFydHdhdGNoJTIwZml0bmVzcyUyMHRyYWNrZXJ8ZW58MXx8fHwxNzYzNDg1MTUwfDA&ixlib=rb-4.1.0&q=80&w=1080"
        )
    )
}
 
