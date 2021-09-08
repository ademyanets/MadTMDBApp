package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.R
import com.demyanets.andrey.mytmdbapp.TmdbService
import com.demyanets.andrey.mytmdbapp.model.MovieDetails
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convertToEntity
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MovieDetailsViewModel(private val state: SavedStateHandle): ViewModel() {

    private val _movie = MutableLiveData<MovieDetails>()
    val movie: LiveData<MovieDetails> = _movie

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var repository: TmdbService? = null

    fun getMovie(id: Int) {
        val client = RetrofitClient.getClient()
        val ds = client.create(TmdbDatasource::class.java)

        ds.getMoviewDetails(id).enqueue( object : Callback<MovieDTO> {
            override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                Log.d("GGG", t.toString())
                _error.value = Exception(t.localizedMessage)
            }
            override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                response.body()?.let {
                    val value: MovieDetails? = it.convertToEntity()
                    if (value != null) {
                        _movie.value = value!!
                    } else {
                        Log.d("GGG", "MovieDetails convertion error")
                        _error.value = Exception("MovieDetails convertion error")
                    }
                }
            }
        })
    }
}