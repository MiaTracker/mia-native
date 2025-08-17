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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import org.nara.mia_native.generated.resources.Res
import org.nara.mia_native.generated.resources.logo
import org.nara.mia_native.generated.resources.tmdbLogoLong

@Composable
fun SettingsAboutView(
    navController: NavHostController,
    drawerState: DrawerState
) {
    SettingsNavigation(
        navController = navController,
        drawerState = drawerState,
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
                Image(
                    painter = painterResource(Res.drawable.tmdbLogoLong),
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    Pair(0f, Color(0xFF90CEA1)),
                                    Pair(0.56f, Color(0xFF3CBEC9)),
                                    Pair(1f, Color(0xFF00B3E5))
                                ),
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                )
                Text(
                    text = "This product uses the TMDB API but is not endorsed or certified by TMDB."
                )
            }
        }
    }
}