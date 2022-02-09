package com.sapir.happybirthday.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sapir.happybirthday.preferences.PreferenceStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage): ViewModel() {

    //saved key as liveData
    val savedKey = preferenceStorage.savedKey().asLiveData()
    fun setSavedKey(key: Boolean) {
        viewModelScope.launch {
            preferenceStorage.setSavedKey(key)
        }
    }

}