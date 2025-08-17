import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import components.TopBar
import infrastructure.Preferences
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import view_models.IndexAdapter
import view_models.MediaDetailsAdapter
import views.InstanceSelectionView
import views.LoginView
import views.MediaDetailsView
import views.MediaIndexList
import views.SettingsProfileView


object Navigation {
    @Serializable
    object InstanceSelection

    @Serializable
    object Login

    @Serializable
    object Inner {
        @Serializable
        object MediaIndex

        @Serializable
        object MoviesIndex

        @Serializable
        object SeriesIndex

        @Serializable
        object Watchlist

        @Serializable
        data class MovieDetails(
            val movieId: Int
        )

        @Serializable
        data class SeriesDetails(
            val seriesId: Int,
        )

        @Serializable
        object Settings {
            @Serializable
            object Profile
        }
    }
}


@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Open)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {

        NavHost(navController, startDestination = Navigation.InstanceSelection) {
            composable<Navigation.InstanceSelection> {
                InstanceSelectionView(
                    navController = navController
                )
            }
            composable<Navigation.Login> {
                LoginView(
                    navController = navController
                )
            }
            navigation<Navigation.Inner>(startDestination = Navigation.Inner.MoviesIndex) {
                composable<Navigation.Inner.MediaIndex> {
                    MediaIndexList(
                        showType = true,
                        navController = navController,
                        drawerState = drawerState,
                        adapter = IndexAdapter.MediaIndexAdapter(navController),
                        title = { Text("Media") }
                    )
                }
                composable<Navigation.Inner.MoviesIndex> {
                    MediaIndexList(
                        showType = false,
                        navController = navController,
                        drawerState = drawerState,
                        adapter = IndexAdapter.MoviesIndexAdapter(navController),
                        title = { Text("Movies") }
                    )
                }
                composable<Navigation.Inner.SeriesIndex> {
                    MediaIndexList(
                        showType = false,
                        navController = navController,
                        drawerState = drawerState,
                        adapter = IndexAdapter.SeriesIndexAdapter(navController),
                        title = { Text("Series") }
                    )
                }
                composable<Navigation.Inner.Watchlist> {
                    MediaIndexList(
                        showType = true,
                        navController = navController,
                        drawerState = drawerState,
                        adapter = IndexAdapter.WatchlistIndexAdapter(navController),
                        title = { Text("Watchlist") }
                    )
                }
                composable<Navigation.Inner.MovieDetails> { backStackEntry ->
                    val movieDetails: Navigation.Inner.MovieDetails = backStackEntry.toRoute()

                    MediaDetailsView(
                        navController = navController,
                        drawerState = drawerState,
                        adapter = MediaDetailsAdapter.MovieDetailsAdapter(movieDetails.movieId),
                    )
                }
                composable<Navigation.Inner.SeriesDetails> { backStackEntry ->
                    val seriesDetails: Navigation.Inner.SeriesDetails = backStackEntry.toRoute()

                    MediaDetailsView(
                        navController = navController,
                        drawerState = drawerState,
                        adapter = MediaDetailsAdapter.SeriesDetailsAdapter(seriesDetails.seriesId),
                    )
                }

                navigation<Navigation.Inner.Settings>(startDestination = Navigation.Inner.Settings.Profile) {
                    composable<Navigation.Inner.Settings.Profile> {
                        SettingsProfileView(
                            navController = navController,
                            drawerState = drawerState
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnerNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    title: @Composable () -> Unit,
    searchbar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val backstack by navController.currentBackStackEntryAsState()
    val isOnRoot =
        backstack?.destination?.hasRoute<Navigation.Inner.MediaIndex>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.MoviesIndex>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.SeriesIndex>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Watchlist>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Settings.Profile>() == true


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            navigationIcon = {
                if (isOnRoot) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                } else {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            },
            title = title,
            searchbar = searchbar,
        )

        ModalNavigationDrawer(
            gesturesEnabled = isOnRoot,
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            NavigationDrawerItem(
                                label = { Text("Media") },
                                icon = { Icon(Icons.Default.PlayArrow, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.MediaIndex>() == true,
                                onClick = {
                                    navController.navigate(Navigation.Inner.MediaIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Movies") },
                                icon = { Icon(Icons.Default.Movie, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.MoviesIndex>() == true,
                                onClick = {
                                    navController.navigate(Navigation.Inner.MoviesIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Series") },
                                icon = { Icon(Icons.Default.Tv, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.SeriesIndex>() == true,
                                onClick = {
                                    navController.navigate(Navigation.Inner.SeriesIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Watchlist") },
                                icon = { Icon(Icons.Default.Schedule, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.Watchlist>() == true,
                                onClick = {
                                    navController.navigate(Navigation.Inner.Watchlist)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                        }

                        Column {
                            NavigationDrawerItem(
                                label = { Text("Settings") },
                                icon = { Icon(Icons.Filled.Settings, null) },
                                selected = false,
                                onClick = {
                                    navController.navigate(Navigation.Inner.Settings)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Logout") },
                                icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                                selected = false,
                                onClick = {
                                    Preferences.Authorization.clear()
                                    navController.navigate(Navigation.Login)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                        }
                    }
                }
            },
            content = {
                content()
            },
            drawerState = drawerState
        )
    }
}
