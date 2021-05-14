package com.mobile.woodmeas.helpers

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import java.sql.Time
import java.util.*

object TimeDateFormatter {
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun getCurrentDateAsString(): String {
        val calendar: Calendar = Calendar.getInstance()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        return simpleDateFormat.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun dateFromCalendarToString(): String {
        val calendar = Calendar.getInstance()
        val year    = calendar.get(Calendar.YEAR)
        val month   = monthStringFormat(calendar.get(Calendar.MONTH))
        val day     = dayStringFormat(calendar.get(Calendar.DAY_OF_MONTH))
        return "$year/$month/$day"
    }

    private fun monthStringFormat(v: Int) : String {
        return if (v < 9) "0${v + 1}" else "${v + 1}"
    }

    private fun dayStringFormat(v: Int): String {
        return if (v < 10) "0$v" else "$v"
    }
}