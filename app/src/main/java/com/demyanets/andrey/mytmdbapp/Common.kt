package com.demyanets.andrey.mytmdbapp

object Common {
    const val GenreKey: String = "genre-id"
    const val MovieKey: String = "movie-id"
    const val TopRatedKey: String = "top-rated"

    fun posterOriginalWidthUrl(path: String) = "https://image.tmdb.org/t/p/original${path}"
    fun poster500WidthUrl(path: String) = "https://image.tmdb.org/t/p/w500${path}"
}