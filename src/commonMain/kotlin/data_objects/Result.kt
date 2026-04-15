package data_objects

import io.ktor.http.HttpStatusCode

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>() {
        override fun unwrapOr(map: (Errors) -> T): T = value

        override fun <R> map(map: (T) -> R): Result<R> = Success(map(value))
    }

    class Error<T>(val errors: Errors) : Result<T>() {
        override fun unwrapOr(map: (Errors) -> T): T = map(errors)

        override fun <R> map(map: (T) -> R): Result<R> = Error(errors)
    }

    abstract fun unwrapOr(map: (Errors) -> T): T

    abstract fun<R> map(map: (T) -> R): Result<R>
}

sealed interface Errors {
    data class ApiErrors(
        val status: HttpStatusCode,
        val apiErrors: ApiErrorList
    ) : Errors

    data class CaughtException(
        val exception: Exception
    ) : Errors
}
