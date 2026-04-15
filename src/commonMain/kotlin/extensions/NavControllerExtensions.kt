package extensions

import androidx.navigation.NavController

fun <T : Any> NavController.navigateFresh(route: T) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}