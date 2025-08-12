package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val query: String,
)
