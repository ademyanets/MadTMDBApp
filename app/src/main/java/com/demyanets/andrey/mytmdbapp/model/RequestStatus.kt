package com.demyanets.andrey.mytmdbapp.model

sealed class RequestStatus {
    class Loading: RequestStatus()
    data class ObjSuccess<T>(val data: T): RequestStatus()
    data class Error(val e: Exception): RequestStatus()
}