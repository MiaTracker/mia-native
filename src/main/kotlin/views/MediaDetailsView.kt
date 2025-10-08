package views

import InnerNavigation
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import components.*
import data_objects.MediaDetails
import infrastructure.ImageSizeInterceptor
import io.ktor.http.*
import view_models.BackdropSelectionUiState
import view_models.MediaDetailsAdapter
import view_models.MediaDetailsUiState
import view_models.MediaDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun<T: MediaDetails> MediaDetailsView(
    navController: NavHostController,
    drawerState: DrawerState,
    adapter: MediaDetailsAdapter<T>,
    viewModel: MediaDetailsViewModel<T> = viewModel {
        MediaDetailsViewModel(adapter)
    }
) {
    val uiState by viewModel.uiState.collectAsState()


    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        title = {
            val s = uiState
            if(s is MediaDetailsUiState.Loaded<T>) Text(text = s.mediaDetails.title)
        }
    ) {
        when (val state = uiState) {
            is MediaDetailsUiState.Loading -> Text("Loading...")
            is MediaDetailsUiState.Loaded<T> -> {

                AnimatedContent(
                    targetState = state.backdropSelectionState
                ) { backdropSelectionState ->
                    if(backdropSelectionState != null) {
                        BackdropSelection(backdropSelectionState, viewModel.Backdrops())
                    }
                    else {
                        MediaDetails(
                            details = state.mediaDetails,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaDetails(details: MediaDetails, viewModel: MediaDetailsViewModel<*>) {
    Scrollable {
        MediaDetailsLayout(
            header = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Backdrop(
                        backdropPath = details.backdropPath,
                        onEdit = { viewModel.Backdrops().openBackdropSelection() }
                    )
                    TitlePanel(
                        details = details,
                        watchlistViewModel = viewModel.Watchlist(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                    )
                }
            },
            poster = {
                Poster(details.posterPath)
            },
            rightContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                ) {

                    TagRows(
                        viewModel = viewModel,
                        details = details
                    )

                    details.overview?.let { overview ->
                        Text(
                            text = overview
                        )
                    }

                    Sources(viewModel.Sources(), details.sources)
                }
            },
            bottomContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Logs(
                        logsViewModel = viewModel.Logs(),
                        logs = details.logs,
                        sources = details.sources
                    )
                }
            }
        )
    }

}

@Composable
fun BackdropSelection(state: BackdropSelectionUiState, viewModel: MediaDetailsViewModel<*>.Backdrops) {
    when(state) {
        is BackdropSelectionUiState.Loading -> {}
        is BackdropSelectionUiState.Loaded -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyFlowRow(
                    width = 310.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.backdrops) { backdrop ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    if(backdrop.current) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .padding(5.dp)
                                .clickable {
                                    viewModel.setBackdrop(backdrop)
                                }
                                .pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            AsyncImage(
                                imageLoader = SingletonImageLoader.get(LocalPlatformContext.current).newBuilder()
                                    .components {
                                        add(ImageSizeInterceptor(ImageSizeInterceptor.ImageType.Poster))
                                    }
                                    .build(),
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(
                                        buildUrl {
                                            takeFrom("https://image.tmdb.org/t/p/original/")
                                            appendPathSegments(backdrop.filePath) //TODO
                                        }.toString()
                                    )
                                    .error {
                                        null
                                    }
                                    //TODO: fallback and err
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(157.dp)
                                    .fillMaxWidth()
                            )
                            Text(
                                text = "${backdrop.width}x${backdrop.height}",
                            )

                            if(backdrop.language != null) {
                                Text(
                                    text = backdrop.language
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.TopStart)
                        .padding(20.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            viewModel.closeBackdropSelection()
                        }
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(7.dp)
                            .size(30.dp)
                    )
                }
            }
        }
    }
}