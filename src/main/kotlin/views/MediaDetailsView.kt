package views

import InnerNavigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.*
import data_objects.MediaDetails
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .wrapContentHeight(),
                ) {

                    MediaDetailsLayout(
                        header = {
                            Column(
                                modifier = Modifier
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Backdrop(details.backdropPath)
                                    TitlePanel(
                                        details = details,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter),
                                    )
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