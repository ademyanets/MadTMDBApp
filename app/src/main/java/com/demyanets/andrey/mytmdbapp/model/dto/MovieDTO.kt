package com.demyanets.andrey.mytmdbapp.model.dto

import com.demyanets.andrey.mytmdbapp.ProductionCountryDTO
import com.demyanets.andrey.mytmdbapp.model.MovieDetails

data class MovieDTO(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection: Any,
    val budget: Int,
    val genres: List<GenreDTO>,
    val homepage: String,
    val id: Int,
    val imdb_id: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompanyDTO>,
    val production_countries: List<ProductionCountryDTO>,
    val release_date: String,
    val revenue: Int,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguageDTO>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

fun MovieDTO.convertToEntity(): MovieDetails? {
    return MovieDetails(id, title, overview, genres, vote_average, production_companies, release_date, backdrop_path, homepage )
}