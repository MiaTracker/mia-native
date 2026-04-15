package components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import data_objects.*
import enums.SourceType
import extensions.toStarsString
import infrastructure.StagingManager
import io.ktor.http.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import view_models.MediaDetailsViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Backdrop(
    backdrop: Image?,
    height: Dp,
    onEdit: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .hoverable(interactionSource)
    ) {
        ApiImage(
            image = backdrop,
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
                        .clickable { onEdit() }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Poster(
    poster: Image?,
    width: Dp,
    height: Dp,
    onEdit: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .hoverable(interactionSource)
    ) {
        ApiImage(
            image = poster,
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
                        .clickable { onEdit() }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitlePanel(details: MediaDetails, posterWidth: Dp, padding: Dp, viewModel: MediaDetailsViewModel<*>, modifier: Modifier = Modifier) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val isStaged = details.id in StagingManager.ids

    Box(
        modifier = modifier.fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(start = posterWidth + 2 * padding, end = padding)
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
                    color = Color.White,
                    modifier = Modifier.weight(2f)
                )

                ExpandingToggleButton(
                    checked = details.onWatchlist,
                    label = "On Watchlist",
                    onCheckedChange = { checked ->
                        if(checked) viewModel.Watchlist().add()
                        else viewModel.Watchlist().remove()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null
                    )
                }

                ExpandingToggleButton(
                    checked = isStaged,
                    onCheckedChange = { StagingManager.toggle(details.id) },
                    label = "Staged",
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    TitlePanelInfo(details)
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
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

                    details.tmdbId?.let { tmdbId ->
                        val uriHandler = LocalUriHandler.current


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(5.dp))
                                .clickable {
                                    uriHandler.openUri(buildUrl {
                                        takeFrom("https://www.themoviedb.org/")
                                        appendPathSegments(
                                            when(details) {
                                                is MovieDetails -> "movie"
                                                is SeriesDetails -> "tv"
                                            }
                                        )
                                        appendPathSegments(details.tmdbId.toString())
                                    }.toString())
                                }
                                .pointerHoverIcon(PointerIcon.Hand)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            TmdbLogoIcon(modifier = Modifier.height(17.dp))
                            Text(text = tmdbId.toString())
                        }
                    }
                }
            }
        }
    }

    if(showDeleteConfirmDialog) {
        ConfirmDialog(
            text = { Text("Confirm media deletion?") },
            onCancel = { showDeleteConfirmDialog = false },
            onConfirm = {
                showDeleteConfirmDialog = false
                viewModel.delete()
            }
        )
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
                    onCommit = { commit() },
                    actions = sequence {
                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Cancel, contentDescription = null) },
                            name = "Cancel",
                            filled = false,
                            callback = ::clear
                        ))

                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                            name = "Add",
                            filled = true,
                            callback = ::commit
                        ))
                    }.toList()
                )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
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
                    onCommit = { commit() },
                    actions = sequence {
                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Cancel, contentDescription = null) },
                            name = "Cancel",
                            filled = false,
                            callback = ::clear
                        ))

                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                            name = "Add",
                            filled = true,
                            callback = ::commit
                        ))
                    }.toList()
                )
            } else {
                InlineIconButton(
                    enabled = sources.any(),
                    onClick = {
                        adding = true
                    }
                ) {
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