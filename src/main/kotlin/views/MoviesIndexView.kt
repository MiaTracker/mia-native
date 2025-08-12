package views

import InnerNavigation
import Navigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import components.SearchBar
import data_objects.MediaIndex
import helpers.toStarsString
import io.ktor.http.*
import view_models.MainUiState
import view_models.MoviesIndexViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MoviesIndexView(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: MoviesIndexViewModel = viewModel { MoviesIndexViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    var str by remember { mutableStateOf("") }

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        title = { Text(text = "Movies") },
        searchbar = {
            SearchBar(
                searchQuery = str,
                onSearchQueryChange = { str = it },
            )
        }
    ) {
        when (val state = uiState) {
            is MainUiState.Loading -> { Text("loading") }
            is MainUiState.Loaded -> {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(10.dp)
                ) {
                    state.media.forEach { media ->
                        MediaIndexView(
                            media = media,
                            modifier = Modifier
                                .onClick {
                                    navController.navigate(Navigation.Inner.MovieDetails(media.id))
                                }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MediaIndexView(media: MediaIndex, modifier: Modifier = Modifier) {
    var hovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .width(160.dp)
            .height(240.dp)
            .pointerHoverIcon(PointerIcon.Hand)
            .onPointerEvent(PointerEventType.Enter) {
                hovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hovered = false
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(
                    buildUrl {
                        takeFrom("https://image.tmdb.org/t/p/original/")
                        appendPathSegments(media.posterPath!!) //TODO
                    }.toString()
                )
                .error {
                    null
                }
                //TODO: fallback and err
                .build(),
            contentDescription = media.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        if(hovered) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(media.stars != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarRate,
                                contentDescription = null,
                                tint = Color.Yellow
                            )

                            Text(
                                text = media.stars.toStarsString(),
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = media.title,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(5.dp)
                )
            }
        }
    }

}