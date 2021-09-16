package com.demyanets.andrey.mytmdbapp.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.demyanets.andrey.mytmdbapp.repository.TmdbApi

//! Dont need that anymore with hilt

class TmdbViewModelFactoryImpl(
    private val api: TmdbApi,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MovieDetailsViewModel(api, handle) as T
    }
}