package com.sapir.happybirthday.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.time.LocalDate
import java.time.Period
import java.util.*

object Utilities {
    fun isStoragePermissionGranted(activity: Activity) {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val integers = intArrayOf(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    listPermissionsNeeded.toTypedArray(), 1
                )
            } else {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                activity.onRequestPermissionsResult(
                    1, listPermissionsNeeded.toTypedArray(), integers
                )
            }
        }
    }

    fun getDate(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month
        cal[Calendar.DAY_OF_MONTH] = day
        return cal.time
    }

    fun getYear(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.YEAR]
    }

    fun getMonth(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.MONTH]
    }

    fun getDay(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.DAY_OF_MONTH]
    }

    fun limitTimePicker(): Long {
        val cStart = Calendar.getInstance()
        cStart[2010, 1] = 1
        return cStart.timeInMillis
    }

    @SuppressLint("NewApi")
    fun getBabyAge(year: Int, month: Int, dayOfMonth: Int): Pair<String,String> {
        val years = Period.between(
            LocalDate.of(year, month, dayOfMonth),
            LocalDate.now()
        ).years
        val months = Period.between(
            LocalDate.of(year, month, dayOfMonth),
            LocalDate.now()
        ).months
        if (months >= 12 && years > 0) {
            return Pair("YEARS", years.toString())
        }
        return Pair("MONTHS", month.toString())
    }
}