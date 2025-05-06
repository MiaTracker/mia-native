package data_objects

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Error<T>(val errors: ApiErrorList) : Result<T>()
}