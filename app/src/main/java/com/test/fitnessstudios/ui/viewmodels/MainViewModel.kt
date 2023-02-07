package com.test.fitnessstudios.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.fitnessstudios.data.models.studio.Studio
import com.test.fitnessstudios.data.repositories.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val mutableStudioDetail = MutableLiveData<Studio?>()
    val studioDetail: LiveData<Studio?> get() = mutableStudioDetail

    private val TAG = "MainViewModel"

    fun setStudioDetail(studio: Studio?) {
        mutableStudioDetail.value = studio
    }

    private val mutableHasLocationPermission = MutableLiveData<Boolean>()
    val hasLocationPermission: LiveData<Boolean> get() = mutableHasLocationPermission

    fun setHasLocationPermission(permission: Boolean) {
        mutableHasLocationPermission.value = permission
    }


    private val mutableLastTabPosition = MutableStateFlow<Int>(0)
    val lastTabPosition: StateFlow<Int> get() = mutableLastTabPosition
    fun setLastTabPosition(position: Int){
        viewModelScope.launch {
            try {
                dataStoreRepository.storeLastTab(position)
            } catch (ioe: IOException) {
                Log.e(TAG, "Failed to store last tab position: $ioe")
            }
        }
    }

    fun getLastTabPosition(): Flow<Int> {
        viewModelScope.launch {
            try {
                dataStoreRepository.getLastTab().collect {
                    mutableLastTabPosition.value = it
                }
            } catch (ioe: IOException) {
                Log.e(TAG, "Failed to store last tab position: $ioe")
            }
        }
        return lastTabPosition
    }
}