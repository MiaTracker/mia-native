package infrastructure

import data_objects.WebConfiguration
import kotlinx.serialization.json.Json
import mia_native.generated.resources.Res

object Configuration {
    private var _config: WebConfiguration? = null

    val config: WebConfiguration
        get() = _config ?: throw Exception("Web config not initialized")


    suspend fun initialize() {
        val json = Res.readBytes("files/config.json").decodeToString()
        _config = Json.decodeFromString<WebConfiguration>(json)
    }
}