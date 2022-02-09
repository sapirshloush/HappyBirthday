package com.sapir.happybirthday.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sapir.happybirthday.util.DateConverter
import java.util.*

@Entity(tableName = "baby_table")
@TypeConverters(DateConverter::class)
data class BabyUser(
    @PrimaryKey(autoGenerate = false)
    val id: Long? = null,
    val name: String? = null,
    val age: String? = null,
    val birthday: Date? = null,
    var imagePath: String? = null,
    val ageType: String? = null
)
