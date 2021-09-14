package com.demyanets.andrey.mytmdbapp.model

sealed class RequestStatus<out T: Any> {
    object Loading: RequestStatus<Nothing>()
    data class ObjSuccess<out T: Any>(val data: T): RequestStatus<T>()
    data class Error(val e: Exception): RequestStatus<Nothing>()
}