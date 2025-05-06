package data_objects

import enums.SourceType
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    val id: Int,
    val name: String,
    val url: String,
    val type: SourceType
)