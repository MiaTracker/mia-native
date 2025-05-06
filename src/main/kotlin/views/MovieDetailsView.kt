package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import view_models.MovieDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
import view_models.MovieDetailsUiState

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
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(
                                    buildUrl {
                                        takeFrom("https://image.tmdb.org/t/p/original/")
                                        appendPathSegments(details.backdropPath!!) //TODO
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

                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.8f))
                                .align(Alignment.BottomCenter),
                        ) {

                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .padding(start = 400.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = details.title,
                                    fontSize = 40.sp,
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
                                            color = Color.White,
                                        )
                                    }
                                    Text(
                                        text = details.releaseDate.year.toString(),
                                        color = Color.White,
                                    )
                                    if(details.originalLanguage != null) {
                                        Text(
                                            text = details.originalLanguage.englishName,
                                            color = Color.White,
                                        )
                                    }
                                    if(details.runtime != null) {
                                        val runtimeDisplay = "${details.runtime / 60}h ${details.runtime % 60}m"

                                        Text(
                                            text = runtimeDisplay,
                                            color = Color.White,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 400.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (title in details.alternativeTitles) {
                                Tag(
                                    actions = {
                                        TagIcon(
                                            onClick = { println("Clicked") }
                                        ) {
                                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                                        }
                                    },
                                    label = {
                                        Text(text =  title.title)
                                    }
                                )
                            }
                        }
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(
                            buildUrl {
                                takeFrom("https://image.tmdb.org/t/p/original/")
                                appendPathSegments(details.posterPath!!) //TODO
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
        }
    }
}

@Composable
fun Tag(actions: @Composable () -> Unit, label: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(10.dp, 2.dp)
        ) {
            label()
            actions()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagIcon(onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .height(17.dp)
            .pointerHoverIcon(icon = PointerIcon.Hand)
            .onClick {
                onClick()
            }
    ) {
        icon()
    }
}