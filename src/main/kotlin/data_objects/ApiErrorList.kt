package data_objects

import kotlinx.serialization.Serializable

typealias ApiErrorList = List<ApiError>

@Serializable
data class ApiError(
    val key: String,
    val debugMessage: String
)