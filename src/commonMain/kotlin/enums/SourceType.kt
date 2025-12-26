package enums

import kotlinx.serialization.SerialName

enum class SourceType {
    @SerialName("torrent") Torrent,
    @SerialName("web") Web,
    @SerialName("jellyfin") Jellyfin
}