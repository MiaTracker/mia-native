package infrastructure

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import data_objects.Errors
import data_objects.Result
import extensions.navigateFresh
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ErrorHandler(
    private val navController: NavController,
) {
    val state = SnackbarHostState()

    suspend fun Result.Error<*>.handle() {
        when(val errors = this.errors) {
            is Errors.ApiErrors -> {
                if(errors.status == HttpStatusCode.Unauthorized) {
                    Preferences.Authorization.clear()

                    withContext(Dispatchers.Main) {
                        navController.navigateFresh(Navigation.Login)
                    }
                }

                withContext(Dispatchers.Default) {
                    for (apiError in errors.apiErrors) {
                        launch {
                            handle(
                                userMessage = "Request to api failed: ${apiError.key}", //TODO: translate
                                logMessage = "Api returned error status ${errors.status.value}: ${apiError.key} - ${apiError.debugMessage}"
                            )
                        }
                    }
                }
            }
            is Errors.CaughtException -> {
                handle(
                    userMessage = "Encountered an unknown error",
                    logMessage = "Exception thrown during api request: ${this.errors.exception}\nStacktrace:\n${this.errors.exception.stackTraceToString()}"
                )
            }
        }
    }

    private suspend fun handle(
        logMessage: String,
        userMessage: String
    ) {
        println(logMessage)
        state.showSnackbar(
            message = userMessage,
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
    }
}