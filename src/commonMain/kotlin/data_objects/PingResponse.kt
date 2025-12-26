package data_objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PingResponse(
    val status: PingStatus
)

@Serializable
enum class PingStatus {
    @SerialName("up") Up
}