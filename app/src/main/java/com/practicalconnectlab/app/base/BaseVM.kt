package com.practicalconnectlab.app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


abstract class BaseVM : ViewModel() {
    val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val loginActiveMessage: MutableLiveData<String> = MutableLiveData()

    init {
        loadingVisibility.value = false
    }

}