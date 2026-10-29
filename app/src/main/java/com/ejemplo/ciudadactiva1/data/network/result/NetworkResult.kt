package com.ejemplo.ciudadactiva1.data.network.result

import retrofit2.Response

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class HttpError(val code: Int, val message: String?, val errorBody: String? = null) : NetworkResult<Nothing>()
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>()
}

suspend inline fun <reified T> safeApiCall(crossinline apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        if (response.isSuccessful) {
            if (body != null) {
                NetworkResult.Success(body)
            } else if (T::class == Unit::class) {
                @Suppress("UNCHECKED_CAST")
                NetworkResult.Success(Unit as T)
            } else {
                NetworkResult.HttpError(response.code(), response.message(), response.errorBody()?.string())
            }
        } else {
            NetworkResult.HttpError(
                code = response.code(),
                message = response.message(),
                errorBody = response.errorBody()?.string()
            )
        }
    } catch (e: java.lang.Exception) {
        NetworkResult.Exception(e)
    }
}
