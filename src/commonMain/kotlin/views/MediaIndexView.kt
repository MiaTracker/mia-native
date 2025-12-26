package views

import InnerNavigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.ApiImage
import components.LazyFlowRow
import components.SearchBar
import data_objects.ExternalMediaIndex
import data_objects.InternalMediaIndex
import data_objects.MediaIndex
import enums.MediaType
import extensions.toStarsString
import infrastructure.ErrorHandler
import view_models.IndexAdapter
import view_models.MediaIndexUiState
import view_models.MediaIndexViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaIndexList(
    showType: Boolean,
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    adapter: IndexAdapter,
    title: @Composable () -> Unit,
    viewModel: MediaIndexViewModel = viewModel {
        MediaIndexViewModel(
            adapter = adapter,
            errorHandler = errorHandler
        )
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler,
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
                val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                val (indexWidth, indexSpacing) =
                    if(windowSizeClass.minWidthDp / 180 >= 4) Pair(180.dp, 15.dp)
                    else Pair(90.dp, 5.dp)

                LazyFlowRow(
                    width = indexWidth,
                    spacing = indexSpacing
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
                                    width = indexWidth,
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
                                    width = indexWidth,
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
fun MediaIndexView(media: MediaIndex, showType: Boolean, width: Dp = 180.dp, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionsSource = remember { MutableInteractionSource() }
    val hovered by interactionsSource.collectIsHoveredAsState()

    val height = (width / 2) * 3

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .hoverable(interactionsSource)
            .let {
                if(media is InternalMediaIndex) {
                    it
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable {
                            onClick()
                        }
                } else it
            }
    ) {
        ApiImage(
            image = media.poster,
            modifier = Modifier
                .fillMaxSize()
        )

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