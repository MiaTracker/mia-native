package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import components.*
import data_objects.MovieDetails
import enums.SourceType
import io.ktor.http.*
import view_models.MovieDetailsUiState
import view_models.MovieDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetailsView(movieId: Int, viewModel: MovieDetailsViewModel = viewModel { MovieDetailsViewModel(movieId) } ) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is MovieDetailsUiState.Loading -> Text("Loading...")
        is MovieDetailsUiState.Loaded -> {

        val details = state.movieDetails

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = 800.dp),
            ) {
                Column {
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

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(start = 400.dp, top = 10.dp, end = 20.dp, bottom = 10.dp)
                    ) {

                        TagRows(
                            viewModel = viewModel,
                            details = details
                        )

                        details.overview?.let { overview ->
                            Text(
                                text = details.overview
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Sources:",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(2f)
                                        ) {
                                            val focusManager = LocalFocusManager.current

                                            var nameText by remember { mutableStateOf("") }
                                            var urlText by remember { mutableStateOf("") }

                                            val focusRequester = remember { FocusRequester() }

                                            InlineTextField(
                                                value = nameText,
                                                onValueChange = { nameText = it },
                                                onDone = { focusManager.moveFocus(FocusDirection.Right) },
                                                placeholder = "Name",
                                                modifier = Modifier
                                                    .focusRequester(focusRequester)
                                                    .weight(2f)
                                            )

                                            var selectedTypeIdx by remember { mutableStateOf(0) }

                                            InlineDropdown(
                                                options = SourceType.entries.map { it.name },
                                                selectedIdx = selectedTypeIdx,
                                                onSelectedIdxChanged = { selectedTypeIdx = it },
                                            )

                                            InlineTextField(
                                                value = urlText,
                                                onValueChange = { urlText = it },
                                                onDone = { },
                                                placeholder = "Url",
                                                modifier = Modifier
                                                        .weight(5f)
                                            )

                                            LaunchedEffect(Unit) {
                                                focusRequester.requestFocus()
                                            }
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                        ) {
                                            TagIcon(
                                                onClick = {}
                                            ) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                            }

                                            AddButton(onClick = {

                                            })
                                        }
                                    }

                                }

                            }

                            SourcesList(
                                sources = details.sources,
                                onEdit = {},
                                onDelete = { source ->
                                    viewModel.Sources().delete(source)
                                }
                            )
                        }
                    }
                }

                Poster(details.posterPath)
            }
        }
    }
}

@Composable
fun Backdrop(backdropPath: String?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(
                buildUrl {
                    takeFrom("https://image.tmdb.org/t/p/original/")
                    appendPathSegments(backdropPath!!) //TODO
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
            .fillMaxWidth()
            .height(500.dp)
    )
}

@Composable
fun Poster(posterPath: String?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(
                buildUrl {
                    takeFrom("https://image.tmdb.org/t/p/original/")
                    appendPathSegments(posterPath!!) //TODO
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
            .width(300.dp)
            .height(450.dp)
            .absoluteOffset(x = 50.dp, y = 350.dp)
    )
}

@Composable
fun TitlePanel(details: MovieDetails, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(start = 400.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = details.title,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                if(details.status != null) {
                    Text(
                        text = details.status,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
                Text(
                    text = details.releaseDate.year.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
                if(details.originalLanguage != null) {
                    Text(
                        text = details.originalLanguage.englishName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
                if(details.runtime != null) {
                    val runtimeDisplay = "${details.runtime / 60}h ${details.runtime % 60}m"

                    Text(
                        text = runtimeDisplay,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun TagRows(viewModel: MovieDetailsViewModel, details: MovieDetails, modifier: Modifier = Modifier) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        TagsRow(
            onAdd = { name ->
                viewModel.AlternativeTitles().create(name)
            }
        ) {
            for (title in details.alternativeTitles) {
                Tag(
                    actions = {
                        TagIcon(
                            onClick = { viewModel.AlternativeTitles().setPrimary(title) }
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null)
                        }

                        TagIcon(
                            onClick = {
                                viewModel.AlternativeTitles().delete(title)
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    },
                    label = {
                        Text(text = title.title)
                    }
                )
            }
        }

        TagsRow(
            onAdd = { name ->
                viewModel.Genres().create(name)
            }
        ) {
            for (genre in details.genres) {
                Tag(
                    actions = {
                        TagIcon(
                            onClick = { viewModel.Genres().delete(genre) }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    },
                    label = { Text(genre.name) }
                )
            }
        }

        TagsRow(
            onAdd = { name ->
                viewModel.Tags().create(name)
            }
        ) {
            for (tag in details.tags) {
                Tag(
                    actions = {
                        TagIcon(
                            onClick = { viewModel.Tags().delete(tag) }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    },
                    label = { Text(tag.name) }
                )
            }
        }
    }
}