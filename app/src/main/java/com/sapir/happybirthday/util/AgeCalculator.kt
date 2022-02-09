package com.sapir.happybirthday.util

import android.annotation.SuppressLint
import android.util.Log
import org.joda.time.Period
import org.joda.time.PeriodType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object AgeCalculator {
    private const val TAG = "AgeCalculator"
    @SuppressLint("SimpleDateFormat")
    fun getBabyAge(birthYear: Int, birthMonth: Int, birthDay: Int): Period {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var birthDate: Date? = null
        var todayDate: Date? = null
        var monthPeriod: Period? = null
        var yearsPeriod: Period? = null
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1
        val day = calendar[Calendar.DAY_OF_MONTH]
        try {
            birthDate = sdf.parse("$birthDay/$birthMonth/$birthYear")
            todayDate = sdf.parse("$day/$month/$year")
        } catch (e: ParseException) {
            Log.e(TAG, e.message!!)
        }
        if (birthDate != null && todayDate != null) {
            val startdate = birthDate.time
            val endDate = todayDate.time
            monthPeriod = Period(startdate, endDate, PeriodType.months())
            yearsPeriod = Period(startdate, endDate, PeriodType.years())
        }

        return if (monthPeriod != null && monthPeriod.months > 12) {
            yearsPeriod!!
        } else {
            monthPeriod!!
        }
    }
}