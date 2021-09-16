package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convert
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbApiRetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TopRatedViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val _items = MutableLiveData<RequestStatus<List<Movie>>>()
    val items: LiveData<RequestStatus<List<Movie>>> = _items

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

        val client = RetrofitClient.getTmdbClient()
        val ds = client.create(TmdbApiRetrofitService::class.java)

        _items.value = RequestStatus.Loading

        ds.getTopRatedList().enqueue(object : Callback<PageResultDTO<ResultDTO>> {
            override fun onFailure(call: Call<PageResultDTO<ResultDTO>>, t: Throwable) {
                Log.d("GGG", t.toString())
                _items.value = RequestStatus.Error(Exception(t.localizedMessage))
                isLoading = false
            }

            override fun onResponse(
                call: Call<PageResultDTO<ResultDTO>>,
                response: Response<PageResultDTO<ResultDTO>>
            ) {
                response.body()?.let {
                    totalPages = it.total_pages
                    val items = it.results.map { it.convert() }.filterNotNull()
                    _items.value = RequestStatus.ObjSuccess(items)
                }
                isLoading = false
            }
        })
    }
}