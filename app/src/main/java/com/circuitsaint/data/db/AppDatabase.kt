package com.circuitsaint.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.circuitsaint.data.model.CartItem
import com.circuitsaint.data.model.Contact
import com.circuitsaint.data.model.Order
import com.circuitsaint.data.model.OrderItem
import com.circuitsaint.data.model.Product

@Database(
    entities = [
        Product::class,
        CartItem::class,
        Order::class,
        OrderItem::class,
        Contact::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun contactDao(): ContactDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "circuit_saint_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Solo para seed data inicial
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

