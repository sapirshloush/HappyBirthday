package com.sapir.happybirthday.preferences

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    //check if baby has saved some details to database, move to birthday screen if saved
    fun savedKey() : Flow<Boolean>
    suspend fun setSavedKey(order: Boolean)

}