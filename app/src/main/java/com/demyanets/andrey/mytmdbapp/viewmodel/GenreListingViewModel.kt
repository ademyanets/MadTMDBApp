package com.demyanets.andrey.mytmdbapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.TmdbService
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import java.lang.Exception

class GenreListingViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val _items = MutableLiveData<Array<ResultDTO>>()
    val items: LiveData<Array<ResultDTO>> = _items

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var repo: TmdbService? = null
    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isLoading: Boolean = false
    private var genre: Int? = null

    fun setRepositoryAndLoadFirstPage(repo: NetworkRepository, genre: Int) {
        this.repo = repo
        this.genre = genre
        loadFirstPage()
    }

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
        if (genre == null) {
            throw ExceptionInInitializerError()
        }

        currentPage = page
        isLoading = true
        repo?.getGenreItems(genre!!, page + 1) {//FIXME: api counts pages starting from 1, not from 0.
            when (it) {
                is RequestResult.EmptyResultSuccess -> TODO()
                is RequestResult.Error -> {
                    _error.value = it.e
                }
                is RequestResult.ObjSuccess<*> -> {
                    val page = it.data as PageResultDTO<ResultDTO>
                    page?.let {
                        page
                        totalPages = page.total_pages
                        val items = page.results.toTypedArray()
                        items?.let {
                            items
                            _items.value = items
                        }
                    }
                }
            }
            isLoading = false
        }
    }
}