package com.demyanets.andrey.mytmdbapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import java.lang.Exception

class MainViewModel(private val state: SavedStateHandle): ViewModel() {
    private val _reloadFlag = MutableLiveData<Unit>()
    val reloadFlag: LiveData<Unit> = _reloadFlag

    fun reload() {
        _reloadFlag.value = Unit
    }
}