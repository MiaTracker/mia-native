package components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Scrollable(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)