package data_objects

import kotlinx.serialization.Serializable


@Serializable
data class SearchResults(
    val indexes: List<InternalMediaIndex>,
    val external: List<ExternalMediaIndex>,
    val queryValid: Boolean
)

@Serializable
data class InternalSearchResults(
    val indexes: List<InternalMediaIndex>,
    val queryValid: Boolean
)