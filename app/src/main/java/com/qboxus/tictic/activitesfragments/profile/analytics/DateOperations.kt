package com.qboxus.tictic.activitesfragments.profile.analytics

import android.text.TextUtils
import android.text.format.DateUtils
import com.qboxus.tictic.Constants
import com.qboxus.tictic.simpleclasses.Functions
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateOperations {

    var dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH)
    var df1Pattern = "yyyy-MM-dd HH:mm:ss"
    var df2Pattern = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    var todayDay = 0

    // change the date into (today ,yesterday and date)
    fun changeDate(date: String): String {
        var date = date
        val cal = Calendar.getInstance()
        todayDay = cal[Calendar.DAY_OF_MONTH]

        //current date in millisecond
        val currenttime = System.currentTimeMillis()

        //database date in millisecond
        var databasedate: Long = 0
        var d: Date? = null
        try {
            val df: DateFormat = SimpleDateFormat(df1Pattern, Locale.ENGLISH)
            d = df.parse(date)
            val simpleDateFormat = SimpleDateFormat("dd, MMM yyyy", Locale.ENGLISH)
            date = simpleDateFormat.format(d)
            databasedate = d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val difference = currenttime - databasedate
        if (difference < 86400000) {
            val chatday = date.substring(0, 2).toInt()
            if (todayDay == chatday) return "Today" else if (todayDay - chatday == 1) return "Yesterday"
        } else if (difference < 172800000) {
            val chatday = date.substring(0, 2).toInt()
            if (todayDay - chatday == 1) return "Yesterday"
        }
        val sdf = SimpleDateFormat("MMM-dd", Locale.ENGLISH)
        return if (d != null) sdf.format(d) else ""
    }

    //This method will change the date format
    fun changeDateFormat(fromFormat: String, toFormat: String, date: String): String {
        val dateFormat = SimpleDateFormat(fromFormat, Locale.ENGLISH)
        var sourceDate: Date? = null
        return try {
            sourceDate = dateFormat.parse(date)
            val targetFormat = SimpleDateFormat(toFormat, Locale.ENGLISH)
            targetFormat.format(sourceDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            Functions.printLog(Constants.tag,"e at date : $e")
            ""
        }
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getDateFromString(fromFormat: String, date: String): Date {
        val dateFormat = SimpleDateFormat(fromFormat, Locale.ENGLISH)
        var sourceDate: Date? = null
        return try {
            sourceDate = dateFormat.parse(date)
            return sourceDate
        } catch (e: ParseException) {
            e.printStackTrace()
           return sourceDate!!
        }
    }

    fun  isCurrentDay(date:String):Boolean{
        if(TextUtils.isEmpty(date))
            return false
        else {
            val date: Date = getDateFromString("yyyy-MM-dd", date)
            return  DateUtils.isToday(date.time)
        }
    }


    fun getDays(startDate: Date, endDate: Date):Long {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
       return elapsedDays
    }

}