package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import components.*
import data_objects.Log
import data_objects.MovieDetails
import data_objects.Source
import enums.SourceType
import helpers.toStarsString
import io.ktor.http.*
import view_models.MovieDetailsUiState
import view_models.MovieDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetailsView(movieId: Int, viewModel: MovieDetailsViewModel = viewModel { MovieDetailsViewModel(movieId) } ) {
    val uiState by viewModel.uiState.collectAsState()

    val density = LocalDensity.current

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

//                SubcomposeLayout { constraints -> //TODO: layout
//
//                     val placeables = subcompose(1) {
//                    Column(
//                        modifier = Modifier
//                    ) {
//                        Box(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Backdrop(details.backdropPath)
//                            TitlePanel(
//                                details = details,
//                                modifier = Modifier
//                                    .align(Alignment.BottomCenter),
//                            )
//                        }
//
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(10.dp),
//                            modifier = Modifier
//                                .padding(start = 340.dp, top = 10.dp, end = 20.dp, bottom = 0.dp)
//                        ) {
//
//                            TagRows(
//                                viewModel = viewModel,
//                                details = details
//                            )
//
//                            details.overview?.let { overview ->
//                                Text(
//                                    text = details.overview
//                                )
//                            }
//
//                            Sources(viewModel.Sources(), details.sources)
//                        }
//                    }
//
//                     }.map { it.measure(constraints) }
//
//                     val baseHeightPx = placeables.sumOf{ it.height }
//
//                     val baseHeight = with(density) { baseHeightPx.toDp() }
//
//                     val offset = max(baseHeight, 800.dp)
//
//                     val dependentPlaceables = subcompose(2) {
//                         Column(
//                             modifier = Modifier
//                                 .padding(horizontal = 20.dp, vertical = 0.dp)
//                                 .background(Color.Red)
//                                 .fillMaxSize()
//                         ) {
//                             Logs(
//                                 logsViewModel = viewModel.Logs(),
//                                 logs = details.logs
//                             )
//                         }
//                     }.map { it.measure(constraints) }
//
//                    val maxWidth = (placeables + dependentPlaceables).maxOfOrNull { it.width } ?: 0
//                    val sumHeight = (placeables + dependentPlaceables).sumOf { it.height }
//
//                    layout(maxWidth, sumHeight) {
//                         placeables.forEach { it.placeRelative(0, 0) }
//                         dependentPlaceables.forEach { it.placeRelative(x = 0, y = offset.toPx().roundToInt()) }
//                    }
//                }

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

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(start = 340.dp, top = 10.dp, end = 20.dp, bottom = 0.dp)
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

                        Sources(viewModel.Sources(), details.sources)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 0.dp)
                        .background(Color.Red)
                        .fillMaxSize()
                ) {
                    Logs(
                        logsViewModel = viewModel.Logs(),
                        logs = details.logs
                    )
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
            .absoluteOffset(x = 20.dp, y = 350.dp)
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
                .padding(start = 340.dp, end = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = details.title,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    details.stars?.let { stars ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.StarRate, contentDescription = null)
                            Text(text = stars.toStarsString())
                        }
                    }


                    details.tmdbVoteAverage?.let { average ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Stars, contentDescription = null)
                            Text(text = average.toStarsString())
                        }
                    }

//                    Icon(imageVector = Res.drawable.tmdbLogo, contentDescription = null) //TODO: show tmdb id
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

@Composable
fun Sources(sourcesViewModel: MovieDetailsViewModel.Sources, sources: List<Source>) {
    data class EditedSource(
        val id: Int?,
        val name: String,
        val type: SourceType,
        val url: String
    )
    var editedSource by remember { mutableStateOf<EditedSource?>(null) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Sources:",
                style = MaterialTheme.typography.titleMedium
            )

            InlineIconButton(onClick = {
                editedSource = EditedSource(
                    id = null,
                    name = "",
                    type = SourceType.Torrent,
                    url = ""
                )
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

        SourcesList(
            sources = sources,
            onEdit = { source ->
                editedSource = EditedSource(
                    id = source.id,
                    name = source.name,
                    type = source.type,
                    url = source.url
                )
            },
            onDelete = { source ->
                sourcesViewModel.delete(source)
            },
            modifier = Modifier
                .padding(vertical = 10.dp)
        )
    }

    editedSource?.let { source ->
        SourceEditDialog(
            name = source.name,
            type = source.type,
            url = source.url,
            onCancelRequest = { editedSource = null },
            onSaveRequest = { name, type, url ->
                if(source.id == null)
                    sourcesViewModel.create(name, type, url)
                else sourcesViewModel.update(source.id, name, type, url)
                editedSource = null
            },
        )
    }
}

@Composable
fun Logs(logsViewModel: MovieDetailsViewModel.Logs, logs: List<Log>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Logs:",
                style = MaterialTheme.typography.titleMedium
            )

            InlineIconButton(onClick = {

            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

//        SourcesList(
//            sources = sources,
//            onEdit = { source ->
//                editedSource = EditedSource(
//                    id = source.id,
//                    name = source.name,
//                    type = source.type,
//                    url = source.url
//                )
//            },
//            onDelete = { source ->
//                sourcesViewModel.delete(source)
//            },
//            modifier = Modifier
//                .padding(vertical = 10.dp)
//        )
    }
}