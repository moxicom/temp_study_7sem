package com.aemarkov.lab9.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DATE_ONLY_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_FORMAT = "dd.MM.yyyy"
    private const val DISPLAY_FORMAT_CARD = "d MMMM yyyy" // 20 ноября 2025
    private const val DISPLAY_FORMAT_WITH_TIME = "dd.MM.yyyy HH:mm"
    private const val PARSE_FORMAT = "dd.MM.yyyy"
    
    private val isoFormatter = SimpleDateFormat(ISO_FORMAT, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val dateOnlyFormatter = SimpleDateFormat(DATE_ONLY_FORMAT, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val displayFormatter = SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault())
    private val displayFormatterCard = SimpleDateFormat(DISPLAY_FORMAT_CARD, Locale("ru", "RU"))
    private val displayFormatterWithTime = SimpleDateFormat(DISPLAY_FORMAT_WITH_TIME, Locale.getDefault())
    private val parseFormatter = SimpleDateFormat(PARSE_FORMAT, Locale.getDefault())
    
    /**
     * Форматирует дату в ISO формат для отправки на сервер.
     * Использует полдень (12:00) в UTC, чтобы избежать сдвига даты при конвертации на сервере.
     * Это гарантирует, что независимо от часового пояса пользователя, дата останется той же.
     */
    fun formatToIso(date: Date): String {
        // Получаем компоненты даты в локальном часовом поясе
        val localCalendar = Calendar.getInstance().apply {
            time = date
        }
        
        // Создаем календарь в UTC с той же датой и временем 12:00
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, localCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, localCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, localCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return isoFormatter.format(utcCalendar.time)
    }
    
    fun formatToIso(calendar: Calendar): String {
        val localCal = calendar.clone() as Calendar
        return formatToIso(localCal.time)
    }
    
    /**
     * Парсит дату из ISO строки.
     * Поддерживает различные форматы ISO 8601.
     * Возвращает Date объект с временем установленным на начало дня в локальном часовом поясе.
     */
    fun parseFromIso(isoString: String): Date? {
        // Различные форматы ISO, которые могут прийти с сервера
        val isoFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm'Z'",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd"
        )
        
        for (format in isoFormats) {
            try {
                val formatter = SimpleDateFormat(format, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = formatter.parse(isoString) ?: continue
                
                // Конвертируем в локальный часовой пояс и устанавливаем время на начало дня
                val calendar = Calendar.getInstance().apply {
                    time = date
                    // Оставляем только дату, время на начало дня
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return calendar.time
            } catch (e: Exception) {
                // Пробуем следующий формат
                continue
            }
        }
        
        return null
    }
    
    /**
     * Парсит дату из формата dd.MM.yyyy.
     * Возвращает Date объект с временем установленным на начало дня в локальном часовом поясе.
     */
    fun parseFromDisplay(dateString: String): Date? {
        return try {
            val date = parseFormatter.parse(dateString) ?: return null
            val calendar = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            calendar.time
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Форматирует дату для отображения в полях ввода (формат: dd.MM.yyyy).
     */
    fun formatForDisplay(date: Date): String {
        return displayFormatter.format(date)
    }
    
    /**
     * Форматирует дату для отображения на карточках (формат: 20 ноября 2025).
     */
    fun formatForCard(date: Date): String {
        return displayFormatterCard.format(date)
    }
    
    /**
     * Форматирует ISO строку для отображения в полях ввода (формат: dd.MM.yyyy).
     */
    fun formatForDisplay(isoString: String): String {
        val date = parseFromIso(isoString) ?: return isoString
        return formatForDisplay(date)
    }
    
    /**
     * Форматирует ISO строку для отображения на карточках (формат: 20 ноября 2025).
     */
    fun formatForCard(isoString: String): String {
        val date = parseFromIso(isoString) ?: return isoString
        return formatForCard(date)
    }
    
    fun formatForDisplayWithTime(date: Date): String {
        return displayFormatterWithTime.format(date)
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

