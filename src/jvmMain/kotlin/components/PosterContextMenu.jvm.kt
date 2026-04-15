package components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
actual fun PosterContextMenu(
    items: List<Pair<String, () -> Unit>>,
    content: @Composable () -> Unit
) {
    DarkDefaultContextMenuRepresentation
    CompositionLocalProvider(LocalContextMenuRepresentation provides DarkDefaultContextMenuRepresentation) {
        ContextMenuArea(items = {
            items.map { (label, action) -> ContextMenuItem(label, action) }
        }) {
            content()
        }
    }
}