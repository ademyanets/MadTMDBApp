package com.demyanets.andrey.mytmdbapp.repository

import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import dagger.hilt.android.scopes.FragmentScoped


interface TmdbApi {
    fun getMovie(id: Int, cb: (res: RequestStatus<MovieDTO>) -> Unit)
}