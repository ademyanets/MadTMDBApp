package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.demyanets.andrey.mytmdbapp.model.MovieDetails
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convertToEntity
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MovieDetailsViewModel(private val state: SavedStateHandle): ViewModel() {
    companion object {
        const val SaveKey = "movie-details-id"
    }

    private val _movie: MutableLiveData<RequestStatus> = MutableLiveData<RequestStatus>()
    val data: LiveData<RequestStatus> = _movie

    private var repo = RetrofitClient.getClient().create(TmdbDatasource::class.java)

    fun getMovie(id: Int) {
        if (!state.contains(SaveKey) || state.get<Int>(SaveKey) != id) {
            doRequest(id)
            state[SaveKey] = id
        }
    }

    private fun doRequest(id: Int) {
        _movie.value = RequestStatus.Loading()
        repo.getMoviewDetails(id).enqueue( object : Callback<MovieDTO> {
            override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                Log.d("GGG", t.toString())
                _movie.value = RequestStatus.Error(Exception(t.localizedMessage))
            }
            override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                response.body()?.let {
                    val value: MovieDetails? = it.convertToEntity()
                    if (value != null) {
                        _movie.value = RequestStatus.ObjSuccess(value!!)
                    } else {
                        Log.d("GGG", "MovieDetails convertion error")
                        _movie.value = RequestStatus.Error(Exception("MovieDetails convertion error"))
                    }
                }
            }
        })
    }
}