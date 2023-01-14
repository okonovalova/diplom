package com.example.diplom.data.network

import org.json.JSONObject
import retrofit2.Response

interface BaseRemoteRepository {
    suspend fun <T, R> getResult(
        request: suspend () -> Response<T>,
        mapTo: (T?) -> R
    ): DataResult<R> {
        return try {
            val result = request()
            if (result.isSuccessful) {
                val resultBody = result.body()
                DataResult.success(mapTo(resultBody))
            } else {
                val json = JSONObject(result.errorBody()?.string().orEmpty())
                val message = json.optString("reason")
                DataResult.error(ResultError(result.code(), message))
            }
        } catch (e: Throwable) {
            DataResult.error(ResultError(0, e.toString()))
        }
    }
}