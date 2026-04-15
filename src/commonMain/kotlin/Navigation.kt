import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import components.BaseScaffold
import components.TopBar
import extensions.navigateFresh
import infrastructure.ErrorHandler
import infrastructure.Preferences
import infrastructure.StagingManager
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import view_models.IndexAdapter
import view_models.MediaDetailsAdapter
import views.*


object Navigation {
    @Serializable
    object InstanceUnreachable

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
        object Staging

        @Serializable
        object Statistics

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

            @Serializable
            object About

            @Serializable
            object Users
        }
    }
}


@Composable
fun RootNavigation(
    onNavHostReady: suspend (NavHostController) -> Unit
) {
    val navController = rememberNavController()

    val errorHandler = remember { ErrorHandler(navController) }

    val drawerState = rememberDrawerState(DrawerValue.Open)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(navController, startDestination = Navigation.InstanceSelection) {
                composable<Navigation.InstanceUnreachable> {
                    InstanceUnreachableView(
                        navController = navController,
                        errorHandler = errorHandler
                    )
                }
                composable<Navigation.InstanceSelection> {
                    InstanceSelectionView(
                        navController = navController,
                        errorHandler = errorHandler
                    )
                }
                composable<Navigation.Login> {
                    LoginView(
                        navController = navController,
                        errorHandler = errorHandler
                    )
                }
                navigation<Navigation.Inner>(startDestination = Navigation.Inner.MoviesIndex) {
                    composable<Navigation.Inner.MediaIndex> {
                        MediaIndexList(
                            showType = true,
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = IndexAdapter.MediaIndexAdapter(navController),
                            title = { Text("Media") }
                        )
                    }
                    composable<Navigation.Inner.MoviesIndex> {
                        MediaIndexList(
                            showType = false,
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = IndexAdapter.MoviesIndexAdapter(navController),
                            title = { Text("Movies") }
                        )
                    }
                    composable<Navigation.Inner.SeriesIndex> {
                        MediaIndexList(
                            showType = false,
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = IndexAdapter.SeriesIndexAdapter(navController),
                            title = { Text("Series") }
                        )
                    }
                    composable<Navigation.Inner.Watchlist> {
                        MediaIndexList(
                            showType = true,
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = IndexAdapter.WatchlistIndexAdapter(navController),
                            title = { Text("Watchlist") }
                        )
                    }
                    composable<Navigation.Inner.Staging> {
                        MediaIndexList(
                            showType = true,
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = IndexAdapter.StagingIndexAdapter(navController),
                            title = { Text("Staged") }
                        )
                    }
                    composable<Navigation.Inner.Statistics> {
                        StatisticsView(
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler
                        )
                    }
                    composable<Navigation.Inner.MovieDetails> { backStackEntry ->
                        val movieDetails: Navigation.Inner.MovieDetails = backStackEntry.toRoute()

                        MediaDetailsView(
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = MediaDetailsAdapter.MovieDetailsAdapter(movieDetails.movieId),
                        )
                    }
                    composable<Navigation.Inner.SeriesDetails> { backStackEntry ->
                        val seriesDetails: Navigation.Inner.SeriesDetails = backStackEntry.toRoute()

                        MediaDetailsView(
                            navController = navController,
                            drawerState = drawerState,
                            errorHandler = errorHandler,
                            adapter = MediaDetailsAdapter.SeriesDetailsAdapter(seriesDetails.seriesId),
                        )
                    }

                    navigation<Navigation.Inner.Settings>(startDestination = Navigation.Inner.Settings.Profile) {
                        composable<Navigation.Inner.Settings.Profile> {
                            SettingsProfileView(
                                navController = navController,
                                drawerState = drawerState,
                                errorHandler = errorHandler
                            )
                        }
                        composable<Navigation.Inner.Settings.About> {
                            SettingsAboutView(
                                navController = navController,
                                drawerState = drawerState,
                                errorHandler = errorHandler
                            )
                        }
                        composable<Navigation.Inner.Settings.Users> {
                            SettingsUsersView(
                                navController = navController,
                                drawerState = drawerState,
                                errorHandler = errorHandler
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnerNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
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
        backstack?.destination?.hasRoute<Navigation.Inner.Staging>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Statistics>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Settings.Profile>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Settings.About>() == true ||
        backstack?.destination?.hasRoute<Navigation.Inner.Settings.Users>() == true


    BaseScaffold(
        errorHandler = errorHandler,
        topBar = {
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
        }
    ) {
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
                                    navController.navigateFresh(Navigation.Inner.MediaIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Movies") },
                                icon = { Icon(Icons.Default.Movie, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.MoviesIndex>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.MoviesIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Series") },
                                icon = { Icon(Icons.Default.Tv, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.SeriesIndex>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.SeriesIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Watchlist") },
                                icon = { Icon(Icons.Default.Schedule, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.Watchlist>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.Watchlist)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            val stagedIds by StagingManager.ids.collectAsState()
                            NavigationDrawerItem(
                                label = { Text("Staged") },
                                icon = { Icon(Icons.Default.Bookmark, null) },
                                badge = { if (stagedIds.isNotEmpty()) Text(stagedIds.size.toString()) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.Staging>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.Staging)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                            NavigationDrawerItem(
                                label = { Text("Statistics") },
                                icon = { Icon(Icons.Default.BarChart, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.Statistics>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.Statistics)
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
                                    navController.navigateFresh(Navigation.Inner.Settings)
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
                                    navController.navigateFresh(Navigation.Login)
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

@Composable
fun SettingsNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    content: @Composable () -> Unit
) {
    val backstack by navController.currentBackStackEntryAsState()

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler,
        title = { Text("Profile") }
    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                ) {
                    Column {
                        NavigationDrawerItem(
                            label = { Text("Profile") },
                            icon = { Icon(Icons.Default.Person, null) },
                            selected = backstack?.destination?.hasRoute<Navigation.Inner.Settings.Profile>() == true,
                            onClick = {
                                navController.navigateFresh(Navigation.Inner.Settings.Profile)
                            },
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                        )
                        NavigationDrawerItem(
                            label = { Text("About") },
                            icon = { Icon(Icons.Default.Info, null) },
                            selected = backstack?.destination?.hasRoute<Navigation.Inner.Settings.About>() == true,
                            onClick = {
                                navController.navigateFresh(Navigation.Inner.Settings.About)
                            },
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                        )

                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "Administration",
                            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        if(Preferences.Authorization.admin) {
                            Spacer(Modifier.height(10.dp))
                            NavigationDrawerItem(
                                label = { Text("Users") },
                                icon = { Icon(Icons.Default.People, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.Settings.Users>() == true,
                                onClick = {
                                    navController.navigateFresh(Navigation.Inner.Settings.Users)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                        }
                    }
                }
            }
        ) {
            content()
        }
    }
}