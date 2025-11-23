package com.circuitsaint.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    fun formatPrice(value: Double): String = currencyFormatter.format(value)
}

