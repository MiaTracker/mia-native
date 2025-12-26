package data_objects

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Log(
    val id: Int,
    val date: LocalDate,
    val source: String,
    val stars: Float?,
    val completed: Boolean,
    val comment: String?,
)

@Serializable
data class LogCreate(
    val date: LocalDate,
    val source: String,
    val stars: Float?,
    val completed: Boolean,
    val comment: String?
)