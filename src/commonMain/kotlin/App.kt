import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavHostController) -> Unit = {}
) {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Box(
            modifier = Modifier.clickable(interactionSource = null, indication = null) { }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                RootNavigation(
                    onNavHostReady = onNavHostReady
                )
            }
        }
    }
}