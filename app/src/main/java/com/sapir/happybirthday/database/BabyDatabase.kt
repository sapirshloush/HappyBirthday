package com.sapir.happybirthday.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sapir.happybirthday.data.BabyDao
import com.sapir.happybirthday.global.ApplicationScope
import com.sapir.happybirthday.model.BabyUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [BabyUser::class], version = 2, exportSchema = false)
abstract class BabyDatabase : RoomDatabase() {

    abstract fun babyDao(): BabyDao

    class Callback @Inject constructor(private val babyDatabase: Provider<BabyDatabase>) : RoomDatabase.Callback() {

        @ApplicationScope private val applicationScope= CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = babyDatabase.get().babyDao()
            applicationScope.launch {

            }
        }
    }
}