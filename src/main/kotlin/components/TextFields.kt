package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun InlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
    placeholder: String? = null,
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
            textAlign = TextAlign.Center
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
                    onDone()
                    return@onKeyEvent true
                } else return@onKeyEvent false
            },
        decorationBox = { innerTextField ->
            if(placeholder != null) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color =
                                if(isFocused) TextFieldDefaults.colors().focusedPlaceholderColor
                                else TextFieldDefaults.colors().unfocusedPlaceholderColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Box(
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        innerTextField()
                    }
                }
            } else {
                innerTextField()
            }
        }
    )
}

@Composable
fun InlineDropdown(
    options: List<String>,
    selectedIdx: Int,
    onSelectedIdxChanged: (Int) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    val baseWidth = options.maxOfOrNull { textMeasurer.measure(it, textStyle).size.width } ?: 0

    val width = max(baseWidth.dp, 100.dp)

    val focusManager = LocalFocusManager.current

    var focused by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    val lowercaseOptions = remember(options) { options.map { it.lowercase() } }

    var focusNextOnRecompose by remember { mutableStateOf(false) }

    fun setText(newText: String) {
        text = newText
        onSelectedIdxChanged(lowercaseOptions.indexOfFirst { it.startsWith(text) })
    }

    fun select(idx: Int) {
        onSelectedIdxChanged(idx)
        text = ""
    }

    fun selectPrevious() {
        if(selectedIdx > 0) {
            select(selectedIdx - 1)
        }
    }

    fun selectNext() {
        if(selectedIdx < options.size - 1) {
            select(selectedIdx + 1)
        }
    }

    fun setFocused(value: Boolean) {
        focused = value
        text = ""
    }

    val positionProvider = remember(selectedIdx) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                return IntOffset(
                    x = anchorBounds.topLeft.x,
                    y = anchorBounds.topLeft.y - (popupContentSize.height / options.size) * selectedIdx
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .onFocusChanged { focusState ->
                setFocused(focusState.hasFocus)
            }
            .clickable {
                setFocused(true)
            }
            .onFocusChanged { focusState ->
                setFocused(focusState.hasFocus)
            }
            .onKeyEvent { event ->
                if(event.isTypedEvent) {
                    val newText = text + Char(event.utf16CodePoint).lowercase()
                    if(lowercaseOptions.any { it.startsWith(newText) })
                        setText(newText)
                }
                else if(event.key == Key.Backspace) {
                    if(event.type == KeyEventType.KeyDown)
                        setText(if(event.isCtrlPressed) String() else text.dropLast(1))
                }
                else if(event.key == Key.DirectionDown) {
                    if(event.type == KeyEventType.KeyDown)
                    selectNext()
                }
                else if(event.key == Key.DirectionUp) {
                    if(event.type == KeyEventType.KeyDown)
                        selectPrevious()
                }
                else if(event.key == Key.Enter) {
                    if(event.type == KeyEventType.KeyDown) {
                        setFocused(false)
                        focusNextOnRecompose = true
                    }
                }
                else return@onKeyEvent false
                true
            }
    ) {

        if(focused) {
            Popup(
                popupPositionProvider = positionProvider,
            ) {
                Box(
                    modifier = Modifier
                        .width(width)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column {
                            for ((idx, option) in options.withIndex()) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color =
                                        if(idx == selectedIdx) MaterialTheme.colorScheme.surfaceContainerHighest
                                        else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            select(idx)
                                            setFocused(false)
                                            focusNextOnRecompose = true
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(5.dp, 2.dp)
                                            .fillMaxWidth()

                                    ) {
                                        Text(
                                            text = buildAnnotatedString {
                                                if (lowercaseOptions[idx].startsWith(text)) {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    ) {
                                                        append(option.take(text.length))
                                                    }
                                                    append(option.takeLast(option.length - text.length))
                                                } else {
                                                    append(option)
                                                }
                                            },
                                            style = textStyle,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            Box(
                modifier = Modifier
                    .padding(5.dp, 2.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = options[selectedIdx],
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if(focusNextOnRecompose) {
        LaunchedEffect(Unit) {
            focusManager.moveFocus(FocusDirection.Right)
        }
        @Suppress("AssignedValueIsNeverRead")
        focusNextOnRecompose = false
    }
}