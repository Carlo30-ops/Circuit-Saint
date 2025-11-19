package com.circuitsaint.util

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log

object PerformanceOptimizer {

    fun optimizeBitmapBytes(resBytes: ByteArray, maxWidth: Int = 800, maxHeight: Int = 800): ByteArray {
        // load bounds
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(resBytes, 0, resBytes.size, opts)
        var (w, h) = opts.outWidth to opts.outHeight
        var inSampleSize = 1
        if (h > maxHeight || w > maxWidth) {
            val halfW = w / 2
            val halfH = h / 2
            while (halfH / inSampleSize >= maxHeight && halfW / inSampleSize >= maxWidth) {
                inSampleSize *= 2
            }
        }
        val opts2 = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        val bmp = BitmapFactory.decodeByteArray(resBytes, 0, resBytes.size, opts2)
        val baos = java.io.ByteArrayOutputStream()
        bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos)
        return baos.toByteArray()
    }

    fun configureMapForLowMemory(context: Context) {
        // Placeholder for any map-specific optimizations if needed
        // e.g. disable heavy map UI when low memory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            if (am.isLowRamDevice) {
                Log.i("PerfOpt", "Low-RAM device: apply light-weight map config")
            }
        }
    }

    fun optimizeCameraForBattery(context: Context) {
        // set camera to lower resolution for scanning if needed
    }
}
