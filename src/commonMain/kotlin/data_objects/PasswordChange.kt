package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class PasswordChange(
    val oldPassword: String,
    val newPassword: String,
    val passwordRepeat: String
)
