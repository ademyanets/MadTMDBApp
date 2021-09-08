package com.demyanets.andrey.mytmdbapp.repository

import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import retrofit2.Call
import retrofit2.http.*

interface TmdbDatasource {
//    @Headers( "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YTY2MjVmZDcxMjEwNTYxZTIyOTYwMTQ2YmMzOWRiOSIsInN1YiI6IjYxMTEyYjMxNGE1MmY4MDA1Y2M3NzBmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.1JrkvhI5zPB-yf6m3cKk6M65xx_UHCVbWTBuJWsY3PA")
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
        @Query("with_genres") ids: Array<Int>,
        @Query("page") page: Int): Call<PageResultDTO<ResultDTO>>

    @GET("movie/{id}")
    fun getMoviewDetails(
        @Path("id") id: Int): Call<MovieDTO>
}
