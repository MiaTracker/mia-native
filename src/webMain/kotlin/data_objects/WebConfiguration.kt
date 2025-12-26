package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class WebConfiguration(
    val instanceUrl: String
)
