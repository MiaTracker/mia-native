package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val uuid: String,
    val username: String,
    val email: String,
    val admin: Boolean
)
