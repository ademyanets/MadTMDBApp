package com.demyanets.andrey.mytmdbapp.viewmodel

import android.net.DnsResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.TmdbService
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TopRatedViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val _items = MutableLiveData<Array<ResultDTO>>()
    val items: LiveData<Array<ResultDTO>> = _items

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var _repo: TmdbService? = null
    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isLoading: Boolean = false

//    fun setRepositoryAndLoadFirstPage(repo: NetworkRepository) {
//        _repo = repo
//        loadPage(0)
//    }

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
                _error.value = Exception(t.localizedMessage)//FIXME:
                isLoading = false
            }

            override fun onResponse(
                call: Call<PageResultDTO<ResultDTO>>,
                response: Response<PageResultDTO<ResultDTO>>
            ) {
                response.body()?.let {
                    totalPages = it.total_pages
                    it.results.toTypedArray()?.let {
                        _items.value = it
                    }
                }
                isLoading = false
            }
        })
    }

//        _repo?.getTopRated(page + 1) {//FIXME: api counts pages starting from 1, not from 0.
//            when (it) {
//                is RequestResult.EmptyResultSuccess -> TODO()
//                is RequestResult.Error -> {
//                    _error.value = it.e
//                }
//                is RequestResult.ObjSuccess<*> -> {
//                    val page = it.data as PageResultDTO<ResultDTO>
//                    page?.let {
//                        page
//                        totalPages = page.total_pages
//                        val items = page.results.toTypedArray()
//                        items?.let {
//                            items
//                            _items.value = items
//                        }
//                    }
//                }
//            }
//            isLoading = false
}