package com.demyanets.andrey.mytmdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convert
import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbApi
import com.demyanets.andrey.mytmdbapp.repository.TmdbApiRetrofitService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class GenreListingViewModel @Inject constructor(
    private val api: TmdbApi,
    private val state: SavedStateHandle
    ) : ViewModel() {

    companion object {
        const val GENRE_KEY = "listing-genre-id"
        const val CurrentPageKey = "listing-current"
        const val TotalPagesKey = "listing-total"
    }

    private val _data = MutableLiveData<RequestStatus<List<Movie>>>()
    val data: LiveData<RequestStatus<List<Movie>>> = _data

    var genre: Genre?
        get() = state.get<Genre>(GENRE_KEY)
        set(value) { state[GENRE_KEY] = value }

    private var currentPage: Int
        get() = state.get<Int>(CurrentPageKey) ?: 0
        set(value) { state[CurrentPageKey] = value }

    private var totalPages: Int
        get() = state.get<Int>(TotalPagesKey) ?: 0
        set(value) { state[TotalPagesKey] = value }

    private var isLoading: Boolean = false//FIXME:

    fun loadFirstPage() {
        loadPage(genre?.id,1)
    }

    fun loadNextPage() {
        if (isLoading) {
            return
        } else if (currentPage == totalPages - 1) {
            return
        }
        loadPage(genre?.id,currentPage + 1)//FIXME:
    }

    private fun loadPage(genre: Int?, page: Int) {
        if (genre == null) {
            throw ExceptionInInitializerError()
        }

        _data.value = RequestStatus.Loading
        currentPage = page
        isLoading = true

        api.getGenreListing(genre, page) {
            when (it) {
                is RequestStatus.Error -> {
                    Log.d("GGG", it.e.localizedMessage)
                    _data.value = RequestStatus.Error(it.e)
                    isLoading = false
                }
                is RequestStatus.ObjSuccess -> {
                    val items = it.data.results.map { it.convert() }.filterNotNull()
                    if (items != null) {
                        totalPages = it.data.total_pages
                        _data.value = RequestStatus.ObjSuccess(items)
                    } else {
                        Log.d("GGG", "Genre ${genre} listing page ${page} conversion error")
                        _data.value =
                            RequestStatus.Error(Exception("Genre ${genre} listing page ${page} conversion error"))
                    }
                    isLoading = false
                }
                RequestStatus.Loading -> TODO()
            }
        }
    }
}
