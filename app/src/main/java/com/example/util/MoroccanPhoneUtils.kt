package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object MoroccanPhoneUtils {
    /**
     * Formats raw Moroccan telephone number into grouped display:
     * "0661234567" -> "06 61 23 45 67"
     * Handles +212 prefixes.
     */
    fun formatMoroccanPhone(raw: String): String {
        var digits = raw.replace("[^0-9+]".toRegex(), "")
        if (digits.startsWith("+212")) {
            digits = "0" + digits.substring(4)
        } else if (digits.startsWith("00212")) {
            digits = "0" + digits.substring(5)
        } else if (digits.startsWith("212")) {
            digits = "0" + digits.substring(3)
        }

        if (digits.length == 10) {
            return "${digits.substring(0, 2)} ${digits.substring(2, 4)} ${digits.substring(4, 6)} ${digits.substring(6, 8)} ${digits.substring(8, 10)}"
        }
        return digits
    }

    /**
     * Cleans phone string to standard 10-digit Moroccan format e.g. "0661234567"
     */
    fun cleanPhone(input: String): String {
        var digits = input.replace("[^0-9+]".toRegex(), "")
        if (digits.startsWith("+212")) {
            digits = "0" + digits.substring(4)
        } else if (digits.startsWith("00212")) {
            digits = "0" + digits.substring(5)
        }
        return digits
    }

    /**
     * Opens phone dialer directly with the customer number.
     */
    fun dialCustomer(context: Context, phone: String) {
        val clean = cleanPhone(phone)
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$clean")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    /**
     * Opens WhatsApp to notify customer that phone is ready or discuss quote.
     */
    fun openWhatsApp(context: Context, phone: String, message: String) {
        val clean = cleanPhone(phone)
        val international = if (clean.startsWith("0")) "212" + clean.substring(1) else clean
        val url = "https://wa.me/$international?text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}
