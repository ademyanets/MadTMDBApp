package com.demyanets.andrey.mytmdbapp.repository

import com.demyanets.andrey.mytmdbapp.Common
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


public interface TmdbDatasource {
    @GET("movie/top_rated")
    fun getTopRatedList(
        //@Query("api_key") apikey: String = Common.tmdbApiKey ): Call<PageResultDTO<ResultDTO>>
        ): Call<PageResultDTO<ResultDTO>>

    @GET("discover/movie")
    fun getGenreItemsList(
        @Query("with_genres") id: Int,
        @Query("page") page: Int): Call<PageResultDTO<ResultDTO>>

    @GET("discover/movie")
    fun getGenresItemsList(
        @Query("with_genres") ids: Array<Integer>,
        @Query("page") page: Int): Call<PageResultDTO<ResultDTO>>

    @GET("movie/{id}")
    fun getMoviewDetails(
        @Path("id") id: Integer,
        @Query("page") page: Int): Call<MovieDTO>
}
