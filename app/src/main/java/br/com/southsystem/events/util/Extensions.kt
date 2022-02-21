package br.com.southsystem.events.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.dateToString(format: String? = "dd/MM/yyyy 'às' HH:mm"): String {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(this.time)
}

fun Double.toBRL(): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = Currency.getInstance("BRL")
    return format.format(this)
}