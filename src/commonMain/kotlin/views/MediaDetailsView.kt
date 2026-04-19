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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import components.*
import components.LoadingSpinner
import data_objects.ImageSource
import data_objects.MediaDetails
import infrastructure.ErrorHandler
import view_models.ImageSelectionUiState
import view_models.MediaDetailsAdapter
import view_models.MediaDetailsUiState
import view_models.MediaDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun<T: MediaDetails> MediaDetailsView(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    adapter: MediaDetailsAdapter<T>,
    viewModel: MediaDetailsViewModel<T> = viewModel {
        MediaDetailsViewModel(
            adapter = adapter,
            navController = navController,
            errorHandler = errorHandler
        )
    }
) {
    val uiState by viewModel.uiState.collectAsState()


    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler,
        title = {
            val s = uiState
            if(s is MediaDetailsUiState.Loaded<T>) Text(text = s.mediaDetails.title)
        }
    ) {
        when (val state = uiState) {
            is MediaDetailsUiState.Loading -> LoadingSpinner()
            is MediaDetailsUiState.Loaded<T> -> {

                AnimatedContent(
                    targetState = state.imageSelectionState,
                    contentKey = { imageState ->
                        when(imageState) {
                            null -> 0
                            is ImageSelectionUiState.Loading -> 1
                            is ImageSelectionUiState.BackdropSelection -> 2
                            is ImageSelectionUiState.PosterSelection -> 3
                        }
                    }
                ) { imageSelectionState ->
                    if(imageSelectionState != null) {
                        ImageSelection(imageSelectionState, viewModel)
                    }
                    else {
                        MediaDetails(
                            details = state.mediaDetails,
                            viewModel = viewModel,
                            pendingItemId = state.pendingItemId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaDetails(details: MediaDetails, viewModel: MediaDetailsViewModel<*>, pendingItemId: Int? = null) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val (posterWidth, padding) =
        if(windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))
            Pair(300.dp, 20.dp)
        else Pair(100.dp, 10.dp)

    val posterHeight = (posterWidth / 2) * 3

    Scrollable {
        MediaDetailsLayout(
            padding = padding,
            header = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Backdrop(
                        backdrop = details.backdrop,
                        height = posterHeight,
                        onEdit = { viewModel.Backdrops().openImageSelection() }
                    )
                    TitlePanel(
                        details = details,
                        viewModel = viewModel,
                        posterWidth = posterWidth,
                        padding = padding,
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                    )
                }
            },
            poster = {
                Poster(
                    poster = details.poster,
                    width = posterWidth,
                    height = posterHeight,
                    onEdit = { viewModel.Posters().openImageSelection() }
                )
            },
            rightContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                ) {

                    TagRows(
                        viewModel = viewModel,
                        details = details,
                        pendingItemId = pendingItemId
                    )

                    details.overview?.let { overview ->
                        Text(
                            text = overview
                        )
                    }

                    Sources(viewModel.Sources(), details.sources, pendingItemId = pendingItemId)
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
                        sources = details.sources,
                        pendingItemId = pendingItemId
                    )
                }
            }
        )
    }

}

@Composable
fun ImageSelection(state: ImageSelectionUiState, viewModel: MediaDetailsViewModel<*>) {

    when(state) {
        is ImageSelectionUiState.Loading -> { LoadingSpinner() }
        is ImageSelectionUiState.LoadedImageSelectionUiState -> {

            val viewModel = when(state) {
                is ImageSelectionUiState.BackdropSelection -> viewModel.Backdrops()
                is ImageSelectionUiState.PosterSelection -> viewModel.Posters()
            }

            val imageSize = when(state) {
                is ImageSelectionUiState.BackdropSelection -> DpSize(300.dp, 157.dp)
                is ImageSelectionUiState.PosterSelection -> DpSize(200.dp, 300.dp)
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyFlowRow(
                    width = imageSize.width + 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.images) { image ->
                        val isPending = state.pendingImagePath == image.path
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    if(image.current) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .padding(5.dp)
                                .let { if(!isPending) it.clickable { viewModel.setImage(image) }.pointerHoverIcon(PointerIcon.Hand) else it }
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(imageSize.height)
                                    .fillMaxWidth()
                            ) {
                                ApiImage(
                                    image = image,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )

                                when(image.source) {
                                    ImageSource.Internal -> {}
                                    ImageSource.TMDB -> {
                                        TmdbLogoIcon(
                                            modifier = Modifier
                                                .width(50.dp)
                                                .padding(10.dp)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                }

                                if(isPending) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            Text(
                                text = "${image.originalWidth}x${image.originalHeight}",
                            )

                            if(image.language != null) {
                                Text(
                                    text = image.language
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
                            viewModel.closeImageSelection()
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