package extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.*

fun Modifier.formFieldKeyEvents(
    focusManager: FocusManager,
    onNext: () -> Unit,
): Modifier = onKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
    when (event.key) {
        Key.Tab if event.isShiftPressed -> { focusManager.moveFocus(FocusDirection.Previous); true }
        Key.Tab -> { focusManager.moveFocus(FocusDirection.Next); true }
        Key.Enter -> { onNext(); true }
        else -> false
    }
}