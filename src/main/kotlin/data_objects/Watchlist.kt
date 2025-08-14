package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class WatchlistChangeBody(
    val mediaId: Int
)