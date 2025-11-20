package com.circuitsaint.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migración de versión 2 a 3
 * Si no hay cambios reales, esta es una migración dummy
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No hay cambios en el esquema entre versión 2 y 3
        // Esta migración es solo para mantener la estructura
        // En el futuro, aquí se agregarían cambios como:
        // database.execSQL("ALTER TABLE products ADD COLUMN new_field TEXT")
    }
}


