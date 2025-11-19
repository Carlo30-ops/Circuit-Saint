package com.circuitsaint.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedidos",
    indices = [
        androidx.room.Index(value = ["estado"]),
        androidx.room.Index(value = ["cliente_email"]),
        androidx.room.Index(value = ["created_at"])
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val numero_pedido: String,
    val cliente_nombre: String,
    val cliente_email: String,
    val cliente_telefono: String? = null,
    val total: Double,
    val estado: String = "pendiente", // pendiente, procesando, enviado, entregado, cancelado
    val notas: String? = null,
    val created_at: Long = System.currentTimeMillis()
)

