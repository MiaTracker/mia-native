package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class GenreCreate(
    val name: String
)