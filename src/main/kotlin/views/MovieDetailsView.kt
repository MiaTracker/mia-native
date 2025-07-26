package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import view_models.MovieDetailsUiState
import view_models.MovieDetailsViewModel
import java.time.ZoneId

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
                                    text = details.overview
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

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
        ) {
            Text(
                text = "Sources:",
                style = MaterialTheme.typography.titleMedium
            )

            var adding by remember { mutableStateOf(false) }

            if (adding) {

                var name by remember { mutableStateOf("") }
                var type by remember { mutableStateOf(SourceType.Torrent) }
                var url by remember { mutableStateOf("") }

                fun clear() {
                    name = ""
                    type = SourceType.Torrent
                    url = ""
                    adding = false
                }

                fun commit() {
                    sourcesViewModel.create(name, type, url)
                    clear()
                }

                SourceTag(
                    editable = true,
                    name = name,
                    onNameChange = { name = it },
                    type = type,
                    onTypeChange = { type = it },
                    url = url,
                    onUrlChange = { url = it },
                    nameWidth = 100.dp,
                    onCommit = { commit() }
                ) {
                    TagIcon(
                        onClick = { clear() }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                    }

                    InlineIconButton(
                        onClick = {
                            commit()
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            if(!adding) {
                InlineIconButton(onClick = {
                    adding = true
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }

        SourcesList(
            sources = sources,
            onUpdate = { source ->
                sourcesViewModel.update(source)
            },
            onDelete = { source ->
                sourcesViewModel.delete(source)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logs(logsViewModel: MovieDetailsViewModel.Logs, logs: List<Log>, sources: List<Source>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxWidth()
        ) {
            Text(
                text = "Logs:",
                style = MaterialTheme.typography.titleMedium
            )

            var adding by remember { mutableStateOf(false) }

            if (adding) {

                var sourceId by remember { mutableStateOf(0) }
                var stars by remember { mutableStateOf<Float?>(null) }
                var completed by remember { mutableStateOf(true) }
                var comment by remember { mutableStateOf<String?>(null) }
                var date by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.UTC).date) }

                fun clear() {
                    sourceId = 0
                    stars = null
                    completed = true
                    comment = null
                    date = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                    adding = false
                }

                fun commit() {
                    logsViewModel.create(
                        date = date,
                        source = sources[sourceId].name,
                        stars = stars,
                        completed = completed,
                        comment = comment
                    )
                    clear()
                }
                
                LogTag(
                    editable = true,
                    sources = sources.map { it.name },
                    sourceId = sourceId,
                    onSourceIdChange = { sourceId = it },
                    stars = stars,
                    onStarsChange = { stars = it },
                    completed = completed,
                    onCompletedChange = { completed = it },
                    comment = comment,
                    onCommentChange = { comment = it },
                    date = date,
                    onDateChange = { date = it },
                    sourceWidth = 100.dp,
                    starsWidth = 100.dp,
                    completedWidth = 100.dp,
                    dateWidth = 100.dp,
                    onCommit = { commit() }
                ) {
                    TagIcon(
                        onClick = { clear() }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                    }

                    InlineIconButton(
                        onClick = {
                            commit()
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            if(!adding) {
                InlineIconButton(onClick = {
                    if(sources.any()) adding = true
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }

        LogsList(
            logs = logs,
            sources = sources,
            onUpdate = { log ->
                logsViewModel.update(log)
            },
            onDelete = { log ->
                logsViewModel.delete(log)
            },
        )
    }
}