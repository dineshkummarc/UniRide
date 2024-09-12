package com.drdisagree.uniride.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Formatter {

    fun getFormattedTime(context: Context, currentTime: Long?): String {
        if (currentTime == null) {
            return "N/A"
        }

        val is24HourFormat = DateFormat.is24HourFormat(context)

        val format = if (is24HourFormat) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

        return format.format(currentTime)
    }
}