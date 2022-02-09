package com.sapir.happybirthday.data

import androidx.room.*
import com.sapir.happybirthday.model.BabyUser
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    /**
     * CREATE
     */

    //insert data to room database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToRoomDatabase(babyUser: BabyUser) : Long

    /**
     * READ
     */

    //get all users inserted to room database...normally this is supposed to be a list of users
    @Transaction
    @Query("SELECT * FROM baby_table ORDER BY id DESC")
    fun getBabyUserDetails() : Flow<List<BabyUser>>

    //get single baby inserted to room database
    @Transaction
    @Query("SELECT * FROM baby_table WHERE id = :id ORDER BY id DESC")
    fun getSingleBabyUserDetails(id: Long) : Flow<BabyUser>

    /**
     * UPDATE
     */

    //update baby details
    @Update
    suspend fun updateBabyUserDetails(babyUser: BabyUser)
}