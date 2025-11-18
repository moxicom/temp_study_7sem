package com.aemarkov.lab9.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DISPLAY_FORMAT = "dd.MM.yyyy"
    private const val DISPLAY_FORMAT_WITH_TIME = "dd.MM.yyyy HH:mm"
    
    private val isoFormatter = SimpleDateFormat(ISO_FORMAT, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val displayFormatter = SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault())
    private val displayFormatterWithTime = SimpleDateFormat(DISPLAY_FORMAT_WITH_TIME, Locale.getDefault())
    
    fun formatToIso(date: Date): String {
        return isoFormatter.format(date)
    }
    
    fun formatToIso(calendar: Calendar): String {
        return formatToIso(calendar.time)
    }
    
    fun parseFromIso(isoString: String): Date? {
        return try {
            isoFormatter.parse(isoString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun formatForDisplay(date: Date): String {
        return displayFormatter.format(date)
    }
    
    fun formatForDisplayWithTime(date: Date): String {
        return displayFormatterWithTime.format(date)
    }
    
    fun formatForDisplay(isoString: String): String {
        val date = parseFromIso(isoString) ?: return isoString
        return formatForDisplay(date)
    }
    
    fun getStartOfDay(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }
    
    fun getEndOfDay(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar
    }
    
    fun getStartOfWeek(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return getStartOfDay(calendar)
    }
    
    fun getEndOfWeek(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        return getEndOfDay(calendar)
    }
    
    fun getStartOfMonth(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar)
    }
    
    fun getEndOfMonth(calendar: Calendar = Calendar.getInstance()): Calendar {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDay(calendar)
    }
}

