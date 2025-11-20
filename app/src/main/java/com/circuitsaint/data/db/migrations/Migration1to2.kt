package com.circuitsaint.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migración de versión 1 a 2
 * Si no hay cambios reales, esta es una migración dummy
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No hay cambios en el esquema entre versión 1 y 2
        // Esta migración es solo para mantener la estructura
    }
}


