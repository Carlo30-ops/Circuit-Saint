package com.circuitsaint.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        androidx.room.Index(value = ["categoria"]),
        androidx.room.Index(value = ["activo"]),
        androidx.room.Index(value = ["stock"])
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val stock: Int = 0,
    val categoria: String = "General",
    val activo: Boolean = true,
    val created_at: Long = System.currentTimeMillis()
)

