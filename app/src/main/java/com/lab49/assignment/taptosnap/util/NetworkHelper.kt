package com.lab49.assignment.taptosnap.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import retrofit2.Response

inline fun <reified ResponseType> networkCall(
    timeout: Long = 10000,
    showLoader: Boolean = true,
    crossinline fetch: suspend () -> Response<ResponseType>,
) = flow<Resource<ResponseType>> {
    try {
        if (showLoader) {
            emit(Resource.Loading())
        }
        withTimeout(timeout) {
            val response: Response<ResponseType> = fetch()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Failure(IllegalStateException(response.message())))
            }
        }
    } catch (exception: Throwable) {
        emit(Resource.Failure(exception))
        if (exception is CancellationException) {
            throw exception
        }
    }
}