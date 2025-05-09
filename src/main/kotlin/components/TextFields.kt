package components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun InlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val interactionsSource = remember { MutableInteractionSource() }

    val isFocused by interactionsSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor),
        textStyle = LocalTextStyle.current.copy(
            color =
                if(isFocused) TextFieldDefaults.colors().focusedTextColor
                else TextFieldDefaults.colors().unfocusedTextColor,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        interactionSource = interactionsSource,
        modifier = modifier
            .padding(10.dp, 1.dp)
            .onKeyEvent { event ->
                if(event.key == Key.Enter) {
                    if(event.type == KeyEventType.KeyDown) onDone()
                    return@onKeyEvent true
                } else return@onKeyEvent false
            }
    )
}