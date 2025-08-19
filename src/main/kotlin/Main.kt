import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import org.nara.mia_native.generated.resources.Res
import org.nara.mia_native.generated.resources.icon

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Box(
            modifier = Modifier.clickable(interactionSource = null, indication = null) { }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                RootNavigation()
            }
        }
    }
}

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            icon = painterResource(Res.drawable.icon)
        ) {
            App()
        }
    }
}
