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
import com.demyanets.andrey.mytmdbapp.repository.TmdbApi
import com.demyanets.andrey.mytmdbapp.repository.TmdbApiRetrofitService
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TopRatedViewModel @Inject constructor(
    private val api: TmdbApi,
    private val state: SavedStateHandle
    ) : ViewModel() {

    private val _data = MutableLiveData<RequestStatus<List<Movie>>>()
    val data: LiveData<RequestStatus<List<Movie>>> = _data

    //FIXME: unify with GenreListingViewModel
    private var currentPage: Int
        get() = state.get<Int>(GenreListingViewModel.CurrentPageKey) ?: 0
        set(value) { state[GenreListingViewModel.CurrentPageKey] = value }

    private var totalPages: Int
        get() = state.get<Int>(GenreListingViewModel.TotalPagesKey) ?: 0
        set(value) { state[GenreListingViewModel.TotalPagesKey] = value }

    private var isLoading: Boolean = false

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

        _data.value = RequestStatus.Loading
        currentPage = page
        isLoading = true

        api.getTopRated(page) {
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
                        Log.d("GGG", "Top rated listing page ${page} conversion error")
                        _data.value =
                            RequestStatus.Error(Exception("Top rated listing page ${page} conversion error"))
                    }
                    isLoading = false
                }
                RequestStatus.Loading -> TODO()
            }
        }
    }
}