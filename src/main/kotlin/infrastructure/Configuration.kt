package infrastructure

import data_objects.ImagesConfiguration
import data_objects.Result

object Configuration {
    private var _instance: ConfigInstance? = null
    private val instance: ConfigInstance
        get() = _instance ?: throw Exception("Configuration not initialized")

    val images: ImagesConfiguration
        get() = instance.images

    suspend fun initialize(errorHandler: ErrorHandler) {
        when(val res = Api.instance.Configuration().images()) {
            is Result.Error<*> -> with(errorHandler) { res.handle() }
            is Result.Success<ImagesConfiguration> -> {
                _instance = ConfigInstance(
                    images = res.value
                )
            }
        }
    }

    data class ConfigInstance(
        val images: ImagesConfiguration
    )
}