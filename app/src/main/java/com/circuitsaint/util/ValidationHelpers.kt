package com.circuitsaint.util

import com.circuitsaint.domain.Result
import java.util.regex.Pattern

/**
 * Extension function para validar email
 */
fun String.isValidEmail(): Boolean {
    if (isEmpty()) return false
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        Pattern.CASE_INSENSITIVE
    )
    return emailPattern.matcher(this).matches()
}

/**
 * Extension function para validar cantidad
 */
fun Int.isValidQuantity(): Boolean {
    return this in Config.MIN_QUANTITY..Config.MAX_QUANTITY
}

/**
 * Extension function para validar precio
 */
fun Double.isValidPrice(): Boolean {
    return this >= 0.0 && this.isFinite()
}

/**
 * Extension function para validar nombre
 */
fun String.isValidName(): Boolean {
    return this.trim().length >= 2 && this.trim().length <= 100
}

/**
 * Extension function para validar teléfono
 */
fun String.isValidPhone(): Boolean {
    val phonePattern = Pattern.compile("^[+]?[0-9]{7,15}$")
    return phonePattern.matcher(this.replace(" ", "").replace("-", "")).matches()
}

/**
 * Validar datos de checkout
 */
fun validateCheckoutData(
    nombre: String,
    email: String,
    telefono: String?
): Result<Unit> {
    return when {
        !nombre.isValidName() -> Result.Error(
            IllegalArgumentException("Nombre inválido"),
            "El nombre debe tener entre 2 y 100 caracteres"
        )
        !email.isValidEmail() -> Result.Error(
            IllegalArgumentException("Email inválido"),
            "Por favor ingresa un email válido"
        )
        telefono != null && !telefono.isValidPhone() -> Result.Error(
            IllegalArgumentException("Teléfono inválido"),
            "El teléfono debe tener entre 7 y 15 dígitos"
        )
        else -> Result.Success(Unit)
    }
}


