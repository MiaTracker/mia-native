package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class MediaImage(
    val current: Boolean,
    val filePath: String,
    val height: Int,
    val width: Int,
    val language: String?
)

@Serializable
data class BackdropUpdate(
    val path: String
)

@Serializable
data class PosterUpdate(
    val path: String
)