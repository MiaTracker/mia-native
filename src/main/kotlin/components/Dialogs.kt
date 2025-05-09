package components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition

@Composable
fun EditDialog(
    onCancelRequest: () -> Unit,
    onSaveRequest: () -> Unit,
    title: String,
    isValid: Boolean,
    content: @Composable () -> Unit
) {
    val state by remember { mutableStateOf(DialogState(
        WindowPosition(Alignment.Center),
        DpSize(500.dp, 300.dp)
    ))}

    var newSize: DpSize? by remember { mutableStateOf(null) }

    DialogWindow(
        onCloseRequest = onCancelRequest,
        state = state,
        visible = true,
        title = title,
        icon = null,
        undecorated = false,
        transparent = false,
        resizable = false,
        enabled = true,
        focusable = true,
        alwaysOnTop = true,
        onKeyEvent = { event ->
            if(event.key == Key.Escape) {
                if(event.type == KeyEventType.KeyDown)
                    onCancelRequest()
                true
            } else false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .onGloballyPositioned { coordinates ->
                    newSize = DpSize(
                        width = coordinates.size.width.dp,
                        height = coordinates.size.height.dp
                    )
                }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    content()
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    FilledTonalButton(
                        onClick = onCancelRequest,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text(text = "Cancel")
                    }

                    Button(
                        onClick = onSaveRequest,
                        enabled = isValid,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }

    newSize?.let { size ->
        state.size = size
        @Suppress("AssignedValueIsNeverRead")
        newSize = null
    }
}