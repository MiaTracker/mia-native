package views

import SettingsNavigation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import components.TmdbLogoLongIcon
import infrastructure.ErrorHandler
import mia_native.generated.resources.Res
import mia_native.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsAboutView(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler
) {
    SettingsNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ){
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(300.dp)
                )
                Text(
                    text = "Movies and TV series tracker",
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Licensed under the terms of AGPL",
                )
                Text(
                    text = buildAnnotatedString {
                        append("Report issues on GitHub: ")
                        withLink(
                            link = LinkAnnotation.Url(url = "https://github.com/MiaTracker/mia-native")
                        ) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("https://github.com/MiaTracker/mia-native")
                            }
                        }
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                TmdbLogoLongIcon(modifier = Modifier.width(100.dp))
                Text(
                    text = "This product uses the TMDB API but is not endorsed or certified by TMDB."
                )
            }
        }
    }
}