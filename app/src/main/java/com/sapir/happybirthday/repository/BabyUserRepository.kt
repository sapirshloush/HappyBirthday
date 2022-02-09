package com.sapir.happybirthday.repository

import com.sapir.happybirthday.data.BabyDao
import com.sapir.happybirthday.model.BabyUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BabyUserRepository  @Inject constructor(private val babyDao: BabyDao) {

    //insert baby details to room
    suspend fun createBabyUserRecord(babyUser: BabyUser) : Long {
        return babyDao.insertToRoomDatabase(babyUser)
    }

    //insert baby details to room
    suspend fun updateBabyUserRecord(babyUser: BabyUser) {
        return babyDao.updateBabyUserDetails(babyUser)
    }

    //get single baby user details
    val getBabyUserDetails: Flow<List<BabyUser>> get() = babyDao.getBabyUserDetails()
}