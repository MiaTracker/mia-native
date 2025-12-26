package data_objects

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResult @OptIn(ExperimentalTime::class) constructor(
    val token: String,
    val expiryDate: Instant,
    val admin: Boolean
)