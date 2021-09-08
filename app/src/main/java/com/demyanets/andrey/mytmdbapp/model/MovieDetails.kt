package com.demyanets.andrey.mytmdbapp.model

import com.demyanets.andrey.mytmdbapp.ProductionCountryDTO
import com.demyanets.andrey.mytmdbapp.model.dto.GenreDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ProductionCompanyDTO
import com.demyanets.andrey.mytmdbapp.model.dto.SpokenLanguageDTO

data class MovieDetails (
    val id: Int,
    val title: String,
    val overview: String,
    val genres: List<GenreDTO>, //FIXME:
    val voteAverage: Double,
    val productionCompanies: List<ProductionCompanyDTO>,
    val releaseDate: String,
    val backdropPath: String,
    val homepage: String,
//    val adult: Boolean,
//    val belongs_to_collection: Any,
//    val budget: Int,
//    val imdb_id: String,
//    val original_language: String,
//    val original_title: String,
//    val popularity: Double,
//    val poster_path: String,
//    val production_countries: List<ProductionCountryDTO>,
//    val revenue: Int,
//    val runtime: Int,
//    val spoken_languages: List<SpokenLanguageDTO>,
//    val status: String,
//    val tagline: String,
//    val video: Boolean,
//    val vote_count: Int
)