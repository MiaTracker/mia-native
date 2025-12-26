import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import infrastructure.Configuration

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
suspend fun main() {
    Configuration.initialize()

    ComposeViewport {
        App(
            onNavHostReady = {
                it.bindToBrowserNavigation()
            }
        )
    }
}