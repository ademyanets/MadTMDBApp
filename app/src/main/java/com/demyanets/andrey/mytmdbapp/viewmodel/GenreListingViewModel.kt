package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class GenreListingViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val _items = MutableLiveData<Array<ResultDTO>>()
    val items: LiveData<Array<ResultDTO>> = _items

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isLoading: Boolean = false
    private var genre: Int? = null

    fun setGenreAndLoad(genre: Int) {
        this.genre = genre
        loadFirstPage()
    }

    fun loadFirstPage() {
        loadPage(1)
    }

    fun loadNextPage() {
        if (isLoading) {
            return
        } else if (currentPage == totalPages - 1) {
            return
        }
        loadPage(currentPage + 1)//FIXME:
    }

    private fun loadPage(page: Int) {
        if (genre == null) {
            throw ExceptionInInitializerError()
        }

        currentPage = page
        isLoading = true

        val client = RetrofitClient.getClient()
        val ds = client.create(TmdbDatasource::class.java)

        ds.getGenreItemsList(genre!!, page).enqueue(object : Callback<PageResultDTO<ResultDTO>> {
            override fun onFailure(call: Call<PageResultDTO<ResultDTO>>, t: Throwable) {
                Log.d("GGG", t.toString())
                _error.value = Exception(t.localizedMessage)
                isLoading = false
            }

            override fun onResponse(
                call: Call<PageResultDTO<ResultDTO>>,
                response: Response<PageResultDTO<ResultDTO>>
            ) {
                response.body()?.let {
                    totalPages = it.total_pages
                    _items.value = emptyArray()
                    it.results.toTypedArray().let { it ->
                        _items.value = it
                    }
                }
                isLoading = false
            }
        })
    }
}