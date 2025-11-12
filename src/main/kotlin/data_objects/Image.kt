package data_objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface ApiImage {
    val path: String
    val sizes: List<ImageSize>
}

@Serializable
data class Image(
    override val path: String,
    override val sizes: List<ImageSize>
) : ApiImage

@Serializable
data class ImageCandidate(
    val current: Boolean,
    override val path: String,
    override val sizes: List<ImageSize>,
    val originalHeight: Int,
    val originalWidth: Int,
    val language: String?,
    val source: ImageSource
) : ApiImage

@Serializable
data class ImageSize(
    val slug: String,
    val width: Int,
    val height: Int,
)

@Serializable
enum class ImageSource {
    @SerialName("internal") Internal,
    @SerialName("tmdb") TMDB
}

@Serializable
data class BackdropUpdate(
    val path: String,
    val source: ImageSource
)

@Serializable
data class PosterUpdate(
    val path: String,
    val source: ImageSource
)