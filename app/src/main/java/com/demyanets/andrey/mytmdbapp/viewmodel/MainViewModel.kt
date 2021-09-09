package com.demyanets.andrey.mytmdbapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(private val state: SavedStateHandle): ViewModel() {
    private val _reloadFlag = MutableLiveData<Unit>()
    val reloadFlag: LiveData<Unit> = _reloadFlag

    fun reload() {
        _reloadFlag.value = Unit
    }
}