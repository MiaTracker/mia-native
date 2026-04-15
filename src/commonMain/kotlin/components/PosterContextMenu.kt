package components

import androidx.compose.runtime.Composable

@Composable
expect fun PosterContextMenu(
    items: List<Pair<String, () -> Unit>>,
    content: @Composable () -> Unit
)