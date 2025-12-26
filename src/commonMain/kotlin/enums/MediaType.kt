package enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    @SerialName("movie") Movie,
    @SerialName("series") Series
}