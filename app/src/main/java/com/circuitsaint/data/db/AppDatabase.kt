package com.circuitsaint.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.circuitsaint.data.db.migrations.MIGRATION_1_2
import com.circuitsaint.data.db.migrations.MIGRATION_2_3
import com.circuitsaint.data.model.CartItem
import com.circuitsaint.data.model.Contact
import com.circuitsaint.data.model.Order
import com.circuitsaint.data.model.OrderItem
import com.circuitsaint.data.model.Product
import com.circuitsaint.util.Config

@Database(
    entities = [
        Product::class,
        CartItem::class,
        Order::class,
        OrderItem::class,
        Contact::class
    ],
    version = Config.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun contactDao(): ContactDao
    
    companion object {
        /**
         * Lista de migraciones disponibles
         * IMPORTANTE: Siempre agregar nuevas migraciones aquí
         * Esta lista se usa en DatabaseModule para configurar Room
         */
        val MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
    }
}

