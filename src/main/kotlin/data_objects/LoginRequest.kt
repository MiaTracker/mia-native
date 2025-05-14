package data_objects

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResult(
    val token: String,
    val expiryDate: Instant,
    val admin: Boolean
)