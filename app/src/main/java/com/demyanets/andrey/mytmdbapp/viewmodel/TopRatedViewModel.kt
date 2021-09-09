package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convert
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TopRatedViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val _items = MutableLiveData<Array<Movie>>()
    val items: LiveData<Array<Movie>> = _items

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isLoading: Boolean = false

    fun loadFirstPage() {
        loadPage(0)
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
        currentPage = page
        isLoading = true

        val client = RetrofitClient.getClient()
        val ds = client.create(TmdbDatasource::class.java)

        ds.getTopRatedList().enqueue(object : Callback<PageResultDTO<ResultDTO>> {
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
                    it.results.toTypedArray()?.let {
                        _items.value = it.map{ it.convert() }.filterNotNull().toTypedArray()
                    }
                }
                isLoading = false
            }
        })
    }
}