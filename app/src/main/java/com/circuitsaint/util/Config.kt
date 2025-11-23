package com.circuitsaint.util

import com.circuitsaint.BuildConfig
import com.google.android.gms.maps.model.LatLng

object Config {
    // Google Maps
    const val GOOGLE_MAPS_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY
    
    // Store Location (Bogotá, Colombia)
    val STORE_LOCATION = LatLng(4.624335, -74.063644)
    const val STORE_NAME = "Circuit Saint — Ubicación Oficial"
    
    // Database
    const val DATABASE_NAME = "circuit_saint_database"
    const val DATABASE_VERSION = 4
    
    // Image Loading
    const val IMAGE_CACHE_SIZE = 50 * 1024 * 1024L // 50MB
    const val IMAGE_MAX_WIDTH = 800
    const val IMAGE_MAX_HEIGHT = 800
    
    // Pagination
    const val PAGE_SIZE = 20
    
    // Validation
    const val MIN_QUANTITY = 1
    const val MAX_QUANTITY = 999
    
    // QR Scanner
    const val QR_SCAN_DEBOUNCE_MS = 1500L
    
    // Performance
    const val LOW_MEMORY_THRESHOLD_MB = 100
}
