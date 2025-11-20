package com.circuitsaint.di

import android.content.Context
import androidx.room.Room
import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.util.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

