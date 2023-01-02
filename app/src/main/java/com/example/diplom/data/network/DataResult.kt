package com.example.diplom.data.network

data class DataResult<out T>(val status: Status, val data: T?, val error: ResultError?) {

    enum class Status {
        SUCCESS,
        ERROR
    }

    companion object {
        fun <T> success(data: T?): DataResult<T> {
            return DataResult(Status.SUCCESS, data, null)
        }

        fun <T> error(error: ResultError?): DataResult<T> {
            return DataResult(Status.ERROR, null, error)
        }
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, error=$error)"
    }
}