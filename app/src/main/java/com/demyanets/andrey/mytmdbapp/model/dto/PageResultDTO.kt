package com.demyanets.andrey.mytmdbapp.model.dto

data class PageResultDTO<T>(
    val page: Int,
    val results: List<T>,
    val total_pages: Int,
    val total_results: Int
)