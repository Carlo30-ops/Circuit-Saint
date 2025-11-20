package com.circuitsaint

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory // Correct import
import com.circuitsaint.util.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CircuitSaintApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // En producción, usar un tree que envíe logs a un servicio
            Timber.plant(Timber.DebugTree()) // TODO: Implementar Crashlytics o similar
        }
        
        Timber.d("Circuit Saint Application initialized")
        
        // Iniciar seeding en background usando WorkManager
        // WorkManager se inicializará automáticamente usando Configuration.Provider
        applicationScope.launch {
            try {
                DatabaseSeeder.startSeeding(this@CircuitSaintApplication)
            } catch (e: Exception) {
                Timber.e(e, "Error iniciando seeding")
            }
        }
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}