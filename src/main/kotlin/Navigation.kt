import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import views.MovieDetailsView
import views.MoviesIndexView

@Serializable
object MoviesIndex

@Serializable
data class MovieDetails(
    val movieId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Open)

    val backstack by navController.currentBackStackEntryAsState()

    val isOnRoot = backstack?.destination?.hasRoute<MoviesIndex>() == true

    Column {
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
                    Column{
                        NavigationDrawerItem(
                            label = { Text("Movies") },
                            icon = { Icon(Icons.Default.Movie, null) },
                            selected = backstack?.destination?.hasRoute<MoviesIndex>() == true,
                            onClick = {
                                navController.navigate(MoviesIndex)
                            }
                        )
                    }
                }
            },
            content = {
                NavHost(navController, startDestination = MoviesIndex) {
                    composable<MoviesIndex> {
                        MoviesIndexView(navController)
                    }
                    composable<MovieDetails> { backStackEntry ->
                        val movieDetails: MovieDetails = backStackEntry.toRoute()
                        MovieDetailsView(movieDetails.movieId)
                    }
                }
            },
            drawerState = drawerState
        )
    }

}