package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.demyanets.andrey.mytmdbapp.model.MovieDetails
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.convertToEntity
import com.demyanets.andrey.mytmdbapp.repository.TmdbApi
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val api: TmdbApi,
    private val state: SavedStateHandle
    ) : ViewModel() {

    companion object {
        const val SaveKey = "movie-details-id"
    }

    private val _movie = MutableLiveData<RequestStatus<MovieDetails>>()
    val data: LiveData<RequestStatus<MovieDetails>> = _movie

    fun getMovie(id: Int) {
        if (!state.contains(SaveKey) || state.get<Int>(SaveKey) != id) {
            doRequest(id)
            state[SaveKey] = id
        }
    }

    private fun doRequest(id: Int) {
        _movie.value = RequestStatus.Loading

        api.getMovie(id) {
            when(it) {
                is RequestStatus.Error -> _movie.value = RequestStatus.Error(it.e)
                is RequestStatus.ObjSuccess -> {
                    val value: MovieDetails? = it.data.convertToEntity()
                    if (value != null) {
                        _movie.value = RequestStatus.ObjSuccess(value!!)
                    } else {
                        Log.d("GGG", "MovieDetails convertion error")
                        _movie.value = RequestStatus.Error(Exception("MovieDetails conversion error"))
                    }
                }
                RequestStatus.Loading -> TODO() //callback status doesn't need this
            }
        }
    }
}