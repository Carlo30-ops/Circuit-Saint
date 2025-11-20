package com.circuitsaint.domain

/**
 * Sealed class para manejar resultados de operaciones
 * Evita retornos null silenciosos y permite manejo explícito de errores
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extension function para mapear Result
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Extension function para obtener el valor o null
 */
fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

/**
 * Extension function para obtener el valor o lanzar excepción
 */
fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> throw exception
        is Result.Loading -> throw IllegalStateException("Result is still loading")
    }
}


