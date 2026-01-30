import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import mia_native.generated.resources.Res
import mia_native.generated.resources.icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource(Res.drawable.icon),
        title = "Mia"
    ) {
        App()
    }
}