import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
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
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import views.InstanceSelectionView
import views.LoginView
import views.MovieDetailsView
import views.MoviesIndexView


object Navigation {
    @Serializable
    object InstanceSelection

    @Serializable
    object Login

    @Serializable
    object Inner {
        @Serializable
        object MoviesIndex

        @Serializable
        data class MovieDetails(
            val movieId: Int
        )
    }
}


@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Open)

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
            composable<Navigation.Inner.MoviesIndex> {
                InnerNavigation(
                    navController = navController,
                    drawerState = drawerState
                ) {
                    MoviesIndexView(navController)
                }
            }
            composable<Navigation.Inner.MovieDetails> { backStackEntry ->
                val movieDetails: Navigation.Inner.MovieDetails = backStackEntry.toRoute()

                InnerNavigation(
                    navController = navController,
                    drawerState = drawerState
                ) {
                    MovieDetailsView(movieDetails.movieId)
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
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val backstack by navController.currentBackStackEntryAsState()
    val isOnRoot = backstack?.destination?.hasRoute<Navigation.Inner.MoviesIndex>() == true


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Movies") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            navigationIcon = {
                if (isOnRoot) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if(drawerState.isOpen) { drawerState.close() }
                                else { drawerState.open() }
                            }
                        },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
                else {
                    IconButton(
                        onClick = {navController.navigateUp()},
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            },
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
                                label = { Text("Movies") },
                                icon = { Icon(Icons.Default.Movie, null) },
                                selected = backstack?.destination?.hasRoute<Navigation.Inner.MoviesIndex>() == true,
                                onClick = {
                                    navController.navigate(Navigation.Inner.MoviesIndex)
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                        }

                        Column {
                            NavigationDrawerItem(
                                label = { Text("Logout") },
                                icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                                selected = false,
                                onClick = {
                                    Api.instance.loginResult = null
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
