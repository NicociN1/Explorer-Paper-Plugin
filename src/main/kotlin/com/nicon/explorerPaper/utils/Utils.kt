package com.nicon.explorerPaper.utils

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.text.NumberFormat
import java.util.Locale

object Utils {
    fun addCommaToNumber(number: Number): String {
        return NumberFormat.getNumberInstance(Locale.US).format(number)
    }

    val amp = LegacyComponentSerializer.legacyAmpersand()
}