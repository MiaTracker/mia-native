package data_objects

import enums.MediaType
import kotlinx.serialization.Serializable

sealed interface MediaIndex {
    val type: MediaType
    val posterPath: String?
    val title: String
}

@Serializable
data class InternalMediaIndex(
    val id: Int,
    val stars: Float?,
    override val type: MediaType,
    override val posterPath: String?,
    override val title: String
) : MediaIndex

@Serializable
data class ExternalMediaIndex(
    val externalId: Int,
    override val posterPath: String?,
    override val title: String,
    override val type: MediaType
) : MediaIndex