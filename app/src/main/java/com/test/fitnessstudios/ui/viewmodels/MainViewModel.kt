package com.test.fitnessstudios.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.fitnessstudios.data.models.Studio

class MainViewModel : ViewModel() {
    private val mutableStudioDetail = MutableLiveData<Studio?>()
    val studioDetail: LiveData<Studio?> get() = mutableStudioDetail

    fun setStudioDetail(studio: Studio?) {
        mutableStudioDetail.value = studio
    }

    private val mutableHasLocationPermission = MutableLiveData<Boolean>()
    val hasLocationPermission: LiveData<Boolean> get() = mutableHasLocationPermission

    fun setHasLocationPermission(permission: Boolean) {
        mutableHasLocationPermission.value = permission
    }
}