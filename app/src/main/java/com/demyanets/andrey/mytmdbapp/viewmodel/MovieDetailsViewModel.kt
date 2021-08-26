package com.demyanets.andrey.mytmdbapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.TmdbService
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import java.lang.Exception

class MovieDetailsViewModel(private val state: SavedStateHandle): ViewModel() {

    private val _movie = MutableLiveData<MovieDTO>()
    val movie: LiveData<MovieDTO> = _movie

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var repository: TmdbService? = null

    fun setRepository(repo: TmdbService) {//FIXME: DI
        repository = repo
    }

    fun getMovie(id: Int) {
        repository?.getMovie(id) {
            when(it) {
                is RequestResult.Error -> _error.value = it.e
                is RequestResult.ObjSuccess<*> -> {
                    (it.data as MovieDTO)?.let { data ->
                        _movie.value = data
                    }
                }
            }
        }
    }
}