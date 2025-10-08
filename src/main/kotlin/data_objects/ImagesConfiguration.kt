package data_objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagesConfiguration(
    val baseUrl: String,
    val secureBaseUrl: String,
    val backdropSizes: List<ImageSize>,
    val logoSizes: List<ImageSize>,
    val posterSizes: List<ImageSize>,
    val profileSizes: List<ImageSize>,
    val stillSizes: List<ImageSize>,
)

@Serializable
data class ImageSize(
    val size: Int?,
    val dimension: ImageSizeDimension,
    val slug: String
)

@Serializable
enum class ImageSizeDimension {
    @SerialName("width") Width,
    @SerialName("height") Height
}
