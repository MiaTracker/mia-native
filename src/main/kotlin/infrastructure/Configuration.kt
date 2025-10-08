package infrastructure

import data_objects.ImagesConfiguration
import data_objects.Result

object Configuration {
    private var _instance: ConfigInstance? = null
    private val instance: ConfigInstance
        get() = _instance ?: throw Exception("Configuration not initialized")

    val images: ImagesConfiguration
        get() = instance.images

    suspend fun initialize() {
        val images = when(val res = Api.instance.Configuration().images()) {
            is Result.Error<*> -> TODO()
            is Result.Success<ImagesConfiguration> -> res.value
        }

        _instance = ConfigInstance(
            images = images
        )
    }

    data class ConfigInstance(
        val images: ImagesConfiguration
    )
}