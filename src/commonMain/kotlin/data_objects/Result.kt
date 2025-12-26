package data_objects

import io.ktor.http.HttpStatusCode

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Error<T>(val errors: Errors) : Result<T>()
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
