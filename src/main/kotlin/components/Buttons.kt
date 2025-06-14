package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InlineIconButton(onClick: () -> Unit, enabled: Boolean = true, icon: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if(enabled) MaterialTheme.colorScheme.primaryContainer else ButtonDefaults.buttonColors().disabledContainerColor,
    ) {
        Box(
            modifier = Modifier
                .pointerHoverIcon(if(enabled) PointerIcon.Hand else PointerIcon.Default)
                .onClick(enabled = enabled) {
                    onClick()
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
            ) {
                icon()
            }
        }
    }
}