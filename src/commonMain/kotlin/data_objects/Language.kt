package data_objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Language(
    @SerialName("iso_639_1") val iso6391: String,
    val englishName: String,
    val name: String
)