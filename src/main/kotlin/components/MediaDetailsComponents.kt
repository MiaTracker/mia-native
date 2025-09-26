package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import data_objects.*
import enums.SourceType
import helpers.toStarsString
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import view_models.MediaDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Backdrop(
    backdropPath: String?,
    onEdit: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .hoverable(interactionSource)
    ) {
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
            modifier = Modifier.fillMaxSize()
        )

        if(isHovered) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .onClick { onEdit() }
                )
            }
        }
    }
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
fun TitlePanel(details: MediaDetails, watchlistViewModel: MediaDetailsViewModel<*>.Watchlist, modifier: Modifier = Modifier) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )

                ExpandingToggleButton(
                    checked = details.onWatchlist,
                    onCheckedChange = { checked ->
                        if(checked) watchlistViewModel.add()
                        else watchlistViewModel.remove()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TitlePanelInfo(details)
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
fun TitlePanelInfo(details: MediaDetails) {
    details.status?.let { status ->
        Text(
            text = status
        )
    }

    when(details) {
        is MovieDetails -> details.releaseDate?.let { releaseDate ->
            Text(
                text = releaseDate.year.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
        is SeriesDetails -> details.firstAirDate?.let { firstAirDate ->
            Text(
                text = firstAirDate.year.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
    }

    details.originalLanguage?.let { originalLanguage ->
        Text(
            text = originalLanguage.englishName,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )
    }

    when(details) {
        is MovieDetails -> details.runtime?.let { runtime ->
            val runtimeDisplay = "${runtime / 60}h ${runtime % 60}m"

            Text(
                text = runtimeDisplay,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
        is SeriesDetails -> {
            details.numberOfSeasons?.let { numberOfSeasons ->
                Text(
                    text = "$numberOfSeasons seasons",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
            details.numberOfEpisodes?.let { numberOfEpisodes ->
                Text(
                    text = "$numberOfEpisodes episodes",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun TagRows(viewModel: MediaDetailsViewModel<*>, details: MediaDetails, modifier: Modifier = Modifier) {

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
fun Sources(sourcesViewModel: MediaDetailsViewModel<*>.Sources, sources: List<Source>) {

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
fun Logs(logsViewModel: MediaDetailsViewModel<*>.Logs, logs: List<Log>, sources: List<Source>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
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
                    starsWidth = 100.dp,
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