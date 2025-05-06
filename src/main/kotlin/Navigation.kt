import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import views.MovieDetailsView
import views.MoviesIndexView
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute

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

    Column {
        TopAppBar(
            title = { Text("Movies") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            navigationIcon = {
                if (backstack?.destination?.hasRoute<MoviesIndex>() == true) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if(drawerState.isOpen) { drawerState.close() }
                                else { drawerState.open() }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
                else {
                    IconButton(
                        onClick = {navController.navigateUp()}
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            },
        )
        ModalNavigationDrawer(
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