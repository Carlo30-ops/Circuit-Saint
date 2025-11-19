package com.circuitsaint.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contactos",
    indices = [
        androidx.room.Index(value = ["leido"]),
        androidx.room.Index(value = ["created_at"])
    ]
)
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val mensaje: String,
    val leido: Boolean = false,
    val respondido: Boolean = false,
    val created_at: Long = System.currentTimeMillis()
)

