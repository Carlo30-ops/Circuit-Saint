package com.circuitsaint.data.model

data class StoreLocation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val hours: String? = null
)

