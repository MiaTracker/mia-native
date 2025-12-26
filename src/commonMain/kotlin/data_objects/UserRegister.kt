package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class UserRegister(
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
)
