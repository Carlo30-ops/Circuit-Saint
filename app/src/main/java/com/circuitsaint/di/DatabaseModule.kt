package com.circuitsaint.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.util.Config
import com.circuitsaint.util.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Config.DATABASE_NAME
        )
            .addMigrations(*AppDatabase.MIGRATIONS)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Ejecutar seeding en un hilo secundario
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = Room.databaseBuilder(
                                context,
                                AppDatabase::class.java,
                                Config.DATABASE_NAME
                            ).build()
                            
                            val productDao = database.productDao()
                            val products = DatabaseSeeder.getSeedProducts()
                            productDao.insertProducts(products)
                            
                            Timber.d("✅ Seeding completado: ${products.size} productos insertados")
                        } catch (e: Exception) {
                            Timber.e(e, "❌ Error en seeding desde Callback")
                        }
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideProductDao(database: AppDatabase) = database.productDao()
    
    @Provides
    fun provideCartDao(database: AppDatabase) = database.cartDao()
    
    @Provides
    fun provideOrderDao(database: AppDatabase) = database.orderDao()
    
    @Provides
    fun provideOrderItemDao(database: AppDatabase) = database.orderItemDao()
    
    @Provides
    fun provideContactDao(database: AppDatabase) = database.contactDao()
}

