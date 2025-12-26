package views

import InnerNavigation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.Scrollable
import data_objects.CategoryStats
import data_objects.InternalMediaIndex
import data_objects.Stats
import infrastructure.ErrorHandler
import infrastructure.generatePalette
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import view_models.StatisticsUiState
import view_models.StatisticsViewModel

@Composable
fun StatisticsView(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    viewModel: StatisticsViewModel = viewModel {
        StatisticsViewModel(
            navController = navController,
            errorHandler = errorHandler
        )
   }
) {
    val uiState by viewModel.uiState.collectAsState()

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler,
        title = { Text("Statistics") },
    ) {
        when(val state = uiState) {
            is StatisticsUiState.Loading -> { Text("Loading") }
            is StatisticsUiState.Loaded -> {
                Scrollable {
                    StatisticsViewContent(
                        stats = state.stats,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsViewContent(
    stats: Stats,
    viewModel: StatisticsViewModel
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val mediaData = remember(stats, primaryColor) {
        val palette = generatePalette(primaryColor, 2)

        listOf(
            Pie(
                label = "Movies",
                data = stats.media.movies.toDouble(),
                color = palette[0]
            ),
            Pie(
                label = "Series",
                data = stats.media.series.toDouble(),
                color = palette[1]
            )
        ).sortedByDescending { it.data }
    }

    val logsData = remember(stats, primaryColor) {
        val palette = generatePalette(primaryColor, 2)

        listOf(
            Pie(
                label = "Completed",
                data = stats.logs.completed.toDouble(),
                color = palette[0]
            ),
            Pie(
                label = "Uncompleted",
                data = stats.logs.uncompleted.toDouble(),
                color = palette[1]
            )
        ).sortedByDescending { it.data }
    }

    val genresData = remember(stats, primaryColor) {
        val palette = generatePalette(primaryColor, stats.genres.size)

        stats.genres.zip(palette).map { (genre, color) ->
            Pie(
                label = genre.name,
                data = genre.count.toDouble(),
                color = color
            )
        }
    }

    val languagesData = remember(stats, primaryColor) {
        val palette = generatePalette(primaryColor, stats.languages.size)

        stats.languages.zip(palette).map { (language, color) ->
            Pie(
                label = language.name,
                data = language.count.toDouble(),
                color = color
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier
            .padding(40.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            PieChartWithLegend(
                title = { Text("Media") },
                data = mediaData,
                showSum = true
            )

            PieChartWithLegend(
                title = { Text("Logs") },
                data = logsData,
                showSum = true
            )

            PieChartWithLegend(
                title = { Text("Genres") },
                data = genresData
            )

            PieChartWithLegend(
                title = { Text("Languages") },
                data = languagesData
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxWidth()
        ) {
            CategoryStatsView(
                title = { Text("Most Watched") },
                stats = stats.mostWatched,
                openMediaDetails = viewModel::openMediaDetails
            )

            CategoryStatsView(
                title = { Text("Highest Rated") },
                stats = stats.highestRated,
                openMediaDetails = viewModel::openMediaDetails
            )
        }
    }
}

@Composable
fun PieChartWithLegend(
    title: @Composable () -> Unit,
    data: List<Pie>,
    showSum: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.titleLarge)

        CompositionLocalProvider(
            value = LocalTextStyle provides mergedStyle
        ) {
            title()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            PieChart(
                modifier = Modifier.size(200.dp)
                    .rotate(-90f),
                data = data,
                selectedScale = 1f,
                style = Pie.Style.Stroke(width = 40.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                for (item in data) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .aspectRatio(2f)
                                .background(item.color)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(text = item.label ?: "", fontWeight = FontWeight.Medium)
                            Text("-")
                            Text("${item.data.toInt()}",)
                        }
                    }
                }

                if(showSum) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .aspectRatio(2f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(text = "Sum", fontWeight = FontWeight.Medium)
                            Text("-")
                            Text("${data.sumOf { it.data }.toInt()}",)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryStatsView(
    title: @Composable () -> Unit,
    stats: CategoryStats,
    openMediaDetails: (InternalMediaIndex) -> Unit
) {
    if(stats.movie == null && stats.series == null) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.titleLarge)

        CompositionLocalProvider(
            value = LocalTextStyle provides mergedStyle
        ) {
            title()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if(stats.movie != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Movie",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    MediaIndexView(
                        media = stats.movie,
                        showType = false,
                        onClick = { openMediaDetails(stats.movie) },
                    )
                }
            }

            if(stats.series != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Series",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    MediaIndexView(
                        media = stats.series,
                        showType = false,
                        onClick = { openMediaDetails(stats.series) },
                    )
                }
            }
        }
    }
}