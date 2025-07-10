package com.pixelpioneer.moneymaster.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for formatting values in a consistent way throughout the app
 */
object FormatUtils {

    /**
     * Format a double value as a currency string
     *
     * @param amount The amount to format
     * @param locale The locale to use for formatting (default is system locale)
     * @return A formatted currency string
     */
    fun formatCurrency(amount: Double, locale: Locale = Locale.getDefault()): String {
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        return numberFormat.format(amount)
    }

    fun formatCurrencyReceipe(amount: Double): String {
        return String.format("%.2f €", amount)
    }

    /**
     * Format a timestamp as a date string
     *
     * @param timestamp The timestamp in milliseconds
     * @param pattern The date pattern to use (default is "dd.MM.yyyy")
     * @param locale The locale to use for formatting (default is system locale)
     * @return A formatted date string
     */
    fun formatDate(
        timestamp: Long,
        pattern: String = "dd.MM.yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat(pattern, locale)
        return dateFormat.format(date)
    }

    /**
     * Format a timestamp as a date and time string
     *
     * @param timestamp The timestamp in milliseconds
     * @param pattern The date and time pattern to use (default is "dd.MM.yyyy HH:mm")
     * @param locale The locale to use for formatting (default is system locale)
     * @return A formatted date and time string
     */
    fun formatDateTime(
        timestamp: Long,
        pattern: String = "dd.MM.yyyy HH:mm",
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat(pattern, locale)
        return dateFormat.format(date)
    }

    /**
     * Format a percentage value
     *
     * @param value The value to format as a percentage (e.g., 0.75 for 75%)
     * @param decimalPlaces The number of decimal places to show (default is 0)
     * @return A formatted percentage string
     */
    fun formatPercentage(value: Double, decimalPlaces: Int = 0): String {
        val format = if (decimalPlaces <= 0) {
            "%.0f%%"
        } else {
            "%." + decimalPlaces + "f%%"
        }
        return String.format(format, value * 100)
    }
}