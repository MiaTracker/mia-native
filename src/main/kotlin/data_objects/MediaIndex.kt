package data_objects

import enums.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaIndex(
    val id: Int,
    val type: MediaType,
    val posterPath: String?,
    val stars: Float?,
    val title: String
)
