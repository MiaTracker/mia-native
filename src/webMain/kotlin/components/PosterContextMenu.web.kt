package components

import androidx.compose.runtime.Composable

@Composable
actual fun PosterContextMenu(
    items: List<Pair<String, () -> Unit>>,
    content: @Composable () -> Unit
) {
    content()
}