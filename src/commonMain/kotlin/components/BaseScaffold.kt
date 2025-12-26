package components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import infrastructure.ErrorHandler

@Composable
fun BaseScaffold(
    errorHandler: ErrorHandler,
    topBar: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Scaffold(
    topBar = topBar,
    snackbarHost = {
        SnackbarHost(
            hostState = errorHandler.state
        ) {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars)
) { innerPadding ->
    Box(
        Modifier.padding(innerPadding)
    ) {
        content()
    }
}