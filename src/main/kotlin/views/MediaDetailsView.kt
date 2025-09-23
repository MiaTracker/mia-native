package views

import InnerNavigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import components.*
import data_objects.MediaDetails
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
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
                val details = state.mediaDetails

                Scrollable {
                    MediaDetailsLayout(
                        header = {
                            if(state.backdropSelectionState == null) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Backdrop(
                                        backdropPath = details.backdropPath,
                                        onEdit = { viewModel.Images().openBackdropSelection() }
                                    )
                                    TitlePanel(
                                        details = details,
                                        watchlistViewModel = viewModel.Watchlist(),
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter),
                                    )
                                }
                            }
                            else {
                                val state = state.backdropSelectionState

                                when(state) {
                                    is BackdropSelectionUiState.Loading -> {}
                                    is BackdropSelectionUiState.Loaded -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                for(backdrop in state.backdrops) {
                                                    AsyncImage(
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
                                                            .width(300.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
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
        }
    }
}