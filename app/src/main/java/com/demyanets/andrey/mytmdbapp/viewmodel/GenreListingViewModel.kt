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
import com.demyanets.andrey.mytmdbapp.repository.TmdbDatasource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class GenreListingViewModel(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        const val GenreKey = "genre-id"
        const val CurrentPageKey = "current-id"
        const val TotalPagesKey = "total-id"
    }

    private val _data = MutableLiveData<RequestStatus>()//MutableLiveData<Array<Movie>>()
    val data: LiveData<RequestStatus> = _data
    private var currentPage: Int
        get() = state.get<Int>(CurrentPageKey) ?: 0
        set(value) { state[CurrentPageKey] = value }

    private var totalPages: Int
        get() = state.get<Int>(TotalPagesKey) ?: 0
        set(value) { state[TotalPagesKey] = value }
    private var isLoading: Boolean = false
    private var repo = RetrofitClient.getClient().create(TmdbDatasource::class.java)

    fun setGenreAndLoad(genre: Int) {
        if (!state.contains(GenreKey) || state.get<Int>(GenreKey) != genre)
        loadFirstPage(genre)
        state[GenreKey] = genre
    }

    fun loadFirstPage(genre: Int) {
        loadPage(genre,1)
    }

    fun loadNextPage(genre: Int) {
        if (isLoading) {
            return
        } else if (currentPage == totalPages - 1) {
            return
        }
        loadPage(genre,currentPage + 1)//FIXME:
    }

    private fun loadPage(genre: Int, page: Int) {
        if (genre == null) {
            throw ExceptionInInitializerError()
        }

        _data.value = RequestStatus.Loading()

        currentPage = page
        isLoading = true

        repo.getGenreItemsList(genre, page).enqueue(object : Callback<PageResultDTO<ResultDTO>> {
            override fun onFailure(call: Call<PageResultDTO<ResultDTO>>, t: Throwable) {
                Log.d("GGG", t.toString())
                _data.value = RequestStatus.Error(Exception(t.localizedMessage))
                isLoading = false
            }

            override fun onResponse(
                call: Call<PageResultDTO<ResultDTO>>,
                response: Response<PageResultDTO<ResultDTO>>
            ) {
                response.body()?.let {
                    totalPages = it.total_pages
                    _data.value = RequestStatus.ObjSuccess<Movie>(emptyArray())
                    it.results.toTypedArray().let { dtoItems ->
                        val items = dtoItems.map { it.convert() }.filterNotNull()
                        _data.value = RequestStatus.ObjSuccess<Movie>(items)
                    }
                }
                isLoading = false
            }
        })
    }
}
