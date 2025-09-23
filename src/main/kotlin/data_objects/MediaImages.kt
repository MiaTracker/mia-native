package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class MediaImages(
    val backdrops: List<MediaImage>,
    val posters: List<MediaImage>
)

@Serializable
data class MediaImage(
    val current: Boolean,
    val filePath: String,
    val height: Int,
    val width: Int,
    val language: String?
)
