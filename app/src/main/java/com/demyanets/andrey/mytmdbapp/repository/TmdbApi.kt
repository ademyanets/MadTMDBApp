package com.demyanets.andrey.mytmdbapp.repository

import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import dagger.hilt.android.scopes.FragmentScoped


interface TmdbApi {
    fun getMovie(id: Int, cb: (res: RequestStatus<MovieDTO>) -> Unit)
    fun getTopRated(page: Int, cb: (res: RequestStatus<PageResultDTO<ResultDTO>>) -> Unit)
    fun getGenreListing(id: Int, page: Int, cb: (res: RequestStatus<PageResultDTO<ResultDTO>>) -> Unit)
}