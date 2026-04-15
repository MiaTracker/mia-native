package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class MediaByIdsQuery(
    val ids: List<Int>,
    val query: String
)
