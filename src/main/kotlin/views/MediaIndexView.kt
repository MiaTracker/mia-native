package views

import InnerNavigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
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
import components.LazyFlowRow
import components.SearchBar
import data_objects.ExternalMediaIndex
import data_objects.InternalMediaIndex
import data_objects.MediaIndex
import enums.MediaType
import helpers.toStarsString
import io.ktor.http.*
import view_models.IndexAdapter
import view_models.MediaIndexUiState
import view_models.MediaIndexViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaIndexList(
    showType: Boolean,
    navController: NavHostController,
    drawerState: DrawerState,
    adapter: IndexAdapter,
    title: @Composable () -> Unit,
    viewModel: MediaIndexViewModel = viewModel { MediaIndexViewModel(adapter) }
) {
    val uiState by viewModel.uiState.collectAsState()

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        title = title,
        searchbar = {
            SearchBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::searchQueryChanged,
                onCommit = viewModel::searchQueryCommited,
                queryValid = when(val state = uiState) {
                    is MediaIndexUiState.Loading -> true
                    is MediaIndexUiState.Loaded -> state.searchQueryValid
                }
            )
        }
    ) {
        when (val state = uiState) {
            is MediaIndexUiState.Loading -> { Text("loading") }
            is MediaIndexUiState.Loaded -> {
                LazyFlowRow(
                    width = 180.dp
                ) {
                    if(state.internal.isNotEmpty()) {
                        items(state.internal) { media ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                MediaIndexView(
                                    media = media,
                                    showType = showType,
                                    onClick = { viewModel.openDetails(media) },
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    if(state.external.isNotEmpty()) {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Text(
                                text = "External:",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        items(state.external) { media ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                MediaIndexView(
                                    media = media,
                                    showType = showType,
                                    onClick = {
                                        viewModel.addExternal(media)
                                    },
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaIndexView(media: MediaIndex, showType: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var hovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .width(180.dp)
            .height(270.dp)
            .onPointerEvent(PointerEventType.Enter) {
                hovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hovered = false
            }
            .let {
                if(media is InternalMediaIndex) {
                    it
                        .pointerHoverIcon(PointerIcon.Hand)
                        .onClick {
                            onClick()
                        }
                } else it
            }
    ) {
        val path = media.posterPath

        if(path != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(
                        buildUrl {
                            takeFrom("https://image.tmdb.org/t/p/original/")
                            appendPathSegments(path)
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
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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


        if(hovered) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(media is InternalMediaIndex && media.stars != null) {
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

                if(media is ExternalMediaIndex) {
                    FilledIconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
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

        if(showType) {
            Icon(
                imageVector = when(media.type) {
                    MediaType.Movie -> Icons.Filled.Movie
                    MediaType.Series -> Icons.Filled.Tv
                },
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}