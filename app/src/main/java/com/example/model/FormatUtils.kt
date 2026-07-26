package com.example.model

import java.util.Locale

fun formatPrice(price: Double): String {
    return try {
        String.format(Locale.US, "%,.0f", price)
    } catch (e: Throwable) {
        price.toLong().toString()
    }
}

fun formatCurrency(amount: Double): String {
    return try {
        String.format(Locale.US, "%,.2f", amount)
    } catch (e: Throwable) {
        amount.toString()
    }
}
