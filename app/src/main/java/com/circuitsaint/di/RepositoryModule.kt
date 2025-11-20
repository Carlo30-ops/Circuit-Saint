package com.circuitsaint.di

import com.circuitsaint.data.db.AppDatabase
import com.circuitsaint.data.repository.StoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStoreRepository(database: AppDatabase): StoreRepository {
        return StoreRepository(database)
    }
}
