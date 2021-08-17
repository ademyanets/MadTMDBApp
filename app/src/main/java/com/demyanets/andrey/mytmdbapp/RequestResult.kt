package com.demyanets.andrey.mytmdbapp

sealed class RequestResult {
    class EmptyResultSuccess: RequestResult()
    data class ObjSuccess<T>(val data: T): RequestResult()
//    data class ListSuccess<T>(val items: [T]): RequestResult()
    data class Error(val e: Exception): RequestResult()
}