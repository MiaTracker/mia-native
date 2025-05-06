package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Int,
    val name: String
)