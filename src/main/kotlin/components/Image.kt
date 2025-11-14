package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import data_objects.ApiImage
import infrastructure.ImageSizeInterceptor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.nara.mia_native.generated.resources.Res
import org.nara.mia_native.generated.resources.tmdbLogo
import org.nara.mia_native.generated.resources.tmdbLogoLong

@Composable
fun TmdbLogoIcon(modifier: Modifier) = TmdbGradientIcon(Res.drawable.tmdbLogo, modifier)

@Composable
fun TmdbLogoLongIcon(modifier: Modifier) = TmdbGradientIcon(Res.drawable.tmdbLogoLong, modifier)

@Composable
private fun TmdbGradientIcon(resource: DrawableResource, modifier: Modifier) = Image(
    painter = painterResource(resource),
    contentDescription = null,
    modifier = modifier
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

@Composable
fun ApiImage(image: ApiImage?, modifier: Modifier = Modifier) {
    var loadFailed by mutableStateOf(false)

    if(image != null && !loadFailed) {
        AsyncImage(
            imageLoader = SingletonImageLoader.get(LocalPlatformContext.current).newBuilder()
                .components {
                    add(ImageSizeInterceptor())
                }
                .build(),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(image)
                .error {
                    loadFailed = true
                    null
                }
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                )
        ) {
            Icon(
                imageVector = Icons.Filled.HideImage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
            )
        }
    }
}
