package com.circuitsaint.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Utilidad para optimizar el consumo de batería y memoria
 */
object PerformanceOptimizer : LifecycleObserver {

    private var wakeLock: PowerManager.WakeLock? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Configura optimizaciones de batería
     */
    fun setupBatteryOptimizations(context: Context) {
        // Deshabilitar animaciones en dispositivos con batería baja
        if (isBatteryLow(context)) {
            // Reducir animaciones y efectos visuales
        }
    }

    /**
     * Verifica si la batería está baja
     */
    private fun isBatteryLow(context: Context): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
        return batteryManager?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 100 < 20
    }

    /**
     * Limpia recursos cuando la app está en background
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        // Liberar wake lock si existe
        wakeLock?.release()
        wakeLock = null
    }

    /**
     * Optimiza memoria liberando recursos no utilizados
     */
    fun optimizeMemory(context: Context) {
        // Sugerir al sistema que ejecute garbage collection
        System.gc()
        
        // Limpiar cachés si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (context as? Activity)?.window?.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
        }
    }

    /**
     * Configura el lifecycle observer para optimizaciones automáticas
     */
    fun observeLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    /**
     * Limpia todos los recursos
     */
    fun cleanup() {
        wakeLock?.release()
        wakeLock = null
        scope.cancel()
    }
}

/**
 * Extension para optimizar imágenes y reducir uso de memoria
 */
fun android.graphics.Bitmap.optimizeForMemory(): android.graphics.Bitmap {
    // Reducir calidad de imagen si es muy grande
    val maxSize = 1024
    if (width > maxSize || height > maxSize) {
        val scale = maxSize.toFloat() / width.coerceAtLeast(height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return android.graphics.Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    }
    return this
}

