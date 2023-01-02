package com.example.diplom.data.network

import retrofit2.Response

abstract class BaseRemoteRepository {
    suspend fun <T, R> getResult(
        request: suspend () -> Response<T>,
        mapTo: (T?) -> R
    ): DataResult<R> {
        return try {
            val result = request.invoke()
            if (result.isSuccessful) {
                val resultBody = result.body()
                DataResult.success(mapTo.invoke(resultBody))
            } else {
                DataResult.error(ResultError(result.code(), result.message()))

            }
        } catch (e: Throwable) {
            DataResult.error(ResultError(0, e.toString()))
        }
    }
}