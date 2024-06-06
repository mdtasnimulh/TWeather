package com.tasnimulhasan.common.dateparser

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateTimeParser {
    fun getCurrentDeviceDateTime(format: String): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(c)
    }

    fun convertLongToReadableDateTime(time: Long, format: String): String {
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(time)
    }

    fun convertLongToDateTime(time: Long, format: String): String {
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(Date(time*1000))
    }

    fun String.convertReadableDateTime(
        dateFormat: String,
        outputFormat: String
    ): String {
        var formatDate = ""
        var sf = SimpleDateFormat(dateFormat, Locale.US)
        try {
            this.let {
                val parseDate = sf.parse(it)
                sf = SimpleDateFormat(outputFormat, Locale.US)
                formatDate = sf.format(parseDate!!)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatDate
    }

    fun addLimitToDate(allowedDay: Int): Long {
        val calendar = Calendar.getInstance()
        try {
            calendar.add(Calendar.DATE, allowedDay)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar.timeInMillis
    }

    fun convertMilliSecondToMinute(milliSecond: Long) = try {
        ((milliSecond / 1000) / 60).toInt()
    } catch (ex: Exception) {
        0
    }

    private fun convertDateFormatToMilliSeconds(dateFormat: String, dateTime: String) =
        SimpleDateFormat(dateFormat, Locale.US).parse(dateTime)?.time ?: 0L

    fun daysDifferenceBetweenTwoDates(
        firstDate: String,
        firstDateFormat: String,
        secondDate: String,
        secondDateFormat: String
    ): Int {
        val firstDateToMillis =
            convertDateFormatToMilliSeconds(dateFormat = firstDateFormat, dateTime = firstDate)
        val secondDateToMillis =
            convertDateFormatToMilliSeconds(dateFormat = secondDateFormat, dateTime = secondDate)

        val diffInMillis =
            if (firstDateToMillis > secondDateToMillis) firstDateToMillis - secondDateToMillis
            else secondDateToMillis - firstDateToMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun String.isDateGetterThanCurrentDate(dateFormat : String){

    }

    fun isDateTodayDate(date: String): String {
        val sdf = SimpleDateFormat(DateTimeFormat.sqlYMD, Locale.US)
        val currentDate = sdf.format(Date())
        return if (currentDate == date) date else ""
    }
}
