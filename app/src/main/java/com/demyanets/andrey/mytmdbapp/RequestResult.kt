package com.demyanets.andrey.mytmdbapp

sealed class RequestResult {
    data class Success(val data: String?): RequestResult()
    data class Error(val e: Exception): RequestResult()
}