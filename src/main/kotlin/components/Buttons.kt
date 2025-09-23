package components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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


@Composable
fun ExpandingToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color =
            if(checked) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        val horizontalPadding by animateDpAsState(targetValue = if(checked) 10.dp else 7.dp)

        Row(
            modifier = Modifier
                .clickable(onClick = { onCheckedChange(!checked) })
                .padding(vertical = 7.dp, horizontal = horizontalPadding)
        ) {
            icon()

            AnimatedVisibility(
                visible = checked,
                enter = slideInHorizontally(initialOffsetX = { it }) + expandHorizontally() + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + shrinkHorizontally() + fadeOut()
            ) {
                Text(
                    text = "On Watchlist",
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        }
    }
}