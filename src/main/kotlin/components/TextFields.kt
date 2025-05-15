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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
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
    alignment: Alignment.Horizontal = Alignment.Start,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val blockAlignment = when(alignment) {
        Alignment.Start -> Alignment.CenterStart
        Alignment.End -> Alignment.CenterEnd
        Alignment.CenterHorizontally -> Alignment.Center
        else -> throw IllegalArgumentException()
    }
    val textAlignment = when(alignment) {
        Alignment.Start -> TextAlign.Start
        Alignment.End -> TextAlign.End
        Alignment.CenterHorizontally -> TextAlign.Center
        else -> throw IllegalArgumentException()
    }

    val colors = MaterialTheme.colorScheme

    val interactionsSource = remember { MutableInteractionSource() }

    val isFocused by interactionsSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor),
            textStyle = LocalTextStyle.current.copy(
                color =
                    if(isFocused) TextFieldDefaults.colors().focusedTextColor
                    else TextFieldDefaults.colors().unfocusedTextColor,
                textAlign = textAlignment
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if(!readOnly) onDone()
                }
            ),
            readOnly = readOnly,
            interactionSource = interactionsSource,
            modifier = Modifier
                .onKeyEvent { event ->
                    if(event.key == Key.Enter) {
                        if(event.type == KeyEventType.KeyDown && !readOnly) onDone()
                        return@onKeyEvent true
                    } else return@onKeyEvent false
                },
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = blockAlignment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .let {
                            if(readOnly) it
                            else {
                                it
                                    .drawBehind {
                                        drawRect(
                                            color = colors.outline,
                                            style = Stroke(width = Stroke.HairlineWidth)
                                        )

                                        if(isFocused) {
                                            drawLine(
                                                color = colors.primary,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = 2f
                                            )
                                        }
                                    }
                            }
                        }
                ) {
                    if(placeholder != null) {
                        if(value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color =
                                    if(isFocused) TextFieldDefaults.colors().focusedPlaceholderColor
                                    else TextFieldDefaults.colors().unfocusedPlaceholderColor,
                                textAlign = textAlignment,
                            )
                        }

                        innerTextField()
                    } else {
                        innerTextField()
                    }
                }
            }
        )
    }
}


@Composable
fun InlineDropdown(
    options: List<String>,
    selectedIdx: Int,
    onSelectedIdxChanged: (Int) -> Unit,
    readOnly: Boolean = false,
) {
    val textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val baseWidth = remember {
        with(density) {
            options.maxOfOrNull { textMeasurer.measure(it, textStyle).size.width }?.toDp() ?: 0.dp
        }
    }

    val colors = MaterialTheme.colorScheme

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
        if(readOnly) {
            focused = false
            return
        }
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
            .width(baseWidth + 10.dp)
            .fillMaxHeight()
            .requiredHeight(28.dp)
            .let {
                if(readOnly) it
                else {
                    it.onFocusChanged { focusState ->
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
                }
            }
    ) {

        if(focused) {
            Popup(
                popupPositionProvider = positionProvider,
            ) {
                Box(
                    modifier = Modifier
                        .width(baseWidth + 10.dp)
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
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                    .fillMaxWidth()
                    .let {
                        if(readOnly) it
                        else {
                            it.drawBehind {
                                drawRect(
                                    color = colors.outline,
                                    style = Stroke(width = Stroke.HairlineWidth)
                                )
                            }
                        }
                    }
                    .padding(horizontal = 5.dp)
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