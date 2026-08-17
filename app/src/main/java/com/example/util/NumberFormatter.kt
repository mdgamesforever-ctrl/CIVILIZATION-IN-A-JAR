package com.example.util

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object NumberFormatter {
    private val SUFFIXES = arrayOf(
        "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No",
        "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod",
        "Vg", "Uvg", "Dvg", "Tvg", "Qavg", "Qivg", "Sxvg", "Spvg", "Ocvg", "Novg", "Cent"
    )

    private val dfShort = DecimalFormat("#,##0.#")
    private val dfTwoDecimals = DecimalFormat("#,##0.00")
    private val dfExact = DecimalFormat("#,##0")

    fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        if (value < 0) return "-" + format(-value)
        if (value < 1000) {
            return if (value == floor(value)) {
                dfExact.format(value)
            } else {
                dfShort.format(value)
            }
        }

        val exponent = (log10(value) / 3).toInt()
        return if (exponent < SUFFIXES.size) {
            val mantissa = value / 10.0.pow((exponent * 3).toDouble())
            if (mantissa >= 100.0) {
                String.format(Locale.US, "%.1f%s", mantissa, SUFFIXES[exponent])
            } else if (mantissa >= 10.0) {
                String.format(Locale.US, "%.1f%s", mantissa, SUFFIXES[exponent])
            } else {
                String.format(Locale.US, "%.2f%s", mantissa, SUFFIXES[exponent])
            }
        } else {
            // Scientific notation for astronomically large numbers
            String.format(Locale.US, "%.2e", value)
        }
    }

    fun formatRate(ratePerSec: Double): String {
        return "+${format(ratePerSec)}/s"
    }

    fun formatYears(years: Double): String {
        return if (years < 1000) {
            "${dfExact.format(years)} yrs"
        } else {
            "${format(years)} yrs"
        }
    }

    fun formatDuration(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return when {
            hrs > 0 -> "${hrs}h ${mins}m ${secs}s"
            mins > 0 -> "${mins}m ${secs}s"
            else -> "${secs}s"
        }
    }
}
