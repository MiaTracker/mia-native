package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class AlternativeTitle(
    val id: Int,
    val title: String,
)