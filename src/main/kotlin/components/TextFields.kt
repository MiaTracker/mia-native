package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import kotlinx.datetime.LocalDate
import java.time.YearMonth

@Composable
fun InlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
    placeholder: String? = null,
    alignment: Alignment.Horizontal = Alignment.Start,
    readOnly: Boolean = false,
    outline: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = textFieldValueState.copy(text = value)

    SideEffect {
        if (
            textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }
    // Last String value that either text field was recomposed with or updated in the onValueChange
    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
    // CoreTextField's onValueChange is called multiple times without recomposition in between.
    var lastTextValue by remember(value) { mutableStateOf(value) }

    InlineTextField(
        value = textFieldValue,
        onValueChange = { newTextFieldValueState ->
            textFieldValueState = newTextFieldValueState

            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
            lastTextValue = newTextFieldValueState.text

            if (stringChangedSinceLastInvocation) {
                onValueChange(newTextFieldValueState.text)
            }
        },
        onDone = onDone,
        placeholder = placeholder,
        alignment = alignment,
        readOnly = readOnly,
        outline = outline,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier
    )
}

@Composable
fun InlineTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onDone: () -> Unit = {},
    placeholder: String? = null,
    alignment: Alignment.Horizontal = Alignment.Start,
    readOnly: Boolean = false,
    outline: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {},
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
            visualTransformation = visualTransformation,
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
                            if(readOnly || !outline) it
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

                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = blockAlignment,
                            modifier = Modifier.weight(2f)
                        ) {
                            if(placeholder != null && value.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color =
                                        if(isFocused) TextFieldDefaults.colors().focusedPlaceholderColor
                                        else TextFieldDefaults.colors().unfocusedPlaceholderColor,
                                    textAlign = textAlignment,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }

                        trailingIcon()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InlineCheckbox(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .requiredHeight(28.dp)
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
            .let {
                if(!readOnly) {
                    it.drawWithCache {
                        onDrawBehind {
                            drawRect(
                                color = colors.outline,
                                style = Stroke(width = Stroke.HairlineWidth)
                            )
                        }
                    }
                } else it
            }
            .onClick(
                enabled = !readOnly
            ) {
                onValueChange(!value)
            }
    ) {
        Icon(
            imageVector = if(value) Icons.Filled.Check else Icons.Filled.Clear,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

//@Composable
//fun InlineDateInput(
//    date: LocalDate,
//    onDateChange: (LocalDate) -> Unit,
//) {
//    var text by remember(date) {
//        mutableStateOf(
//            "%04d".format(date.year) + "%02d".format(date.monthNumber) + "%02d".format(date.dayOfMonth)
//        )
//    }
//
//
//    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
//    // of the composition.
//    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = text)) }
//    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
//    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
//    // composition.
//    val textFieldValue = textFieldValueState.copy(text = text)
//
//    SideEffect {
//        if (
//            textFieldValue.selection != textFieldValueState.selection ||
//            textFieldValue.composition != textFieldValueState.composition
//        ) {
//            textFieldValueState = textFieldValue
//        }
//    }
//    // Last String value that either text field was recomposed with or updated in the onValueChange
//    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
//    // CoreTextField's onValueChange is called multiple times without recomposition in between.
//    var lastTextValue by remember(text) { mutableStateOf(text) }
//
//    InlineTextField(
//        value = textFieldValue,
//        onValueChange = { newTextFieldValueState ->
//
//            println(
//                "(${newTextFieldValueState.selection.start} - ${newTextFieldValueState.selection.end})"
//            )
//
//            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
//
//            if (stringChangedSinceLastInvocation) {
//                if(newTextFieldValueState.text.all { it.isDigit() }) {
//
//                    var output = lastTextValue
//
//                    if(!textFieldValueState.selection.collapsed) {
//                        val selection = textFieldValueState.selection
//
//                        output = output.replaceRange(selection.start, selection.end, "0".repeat(selection.length))
//                    }
//
//                    if(newTextFieldValueState.text.length > lastTextValue.length) {
//                        val addedCount = newTextFieldValueState.text.length - lastTextValue.length
//
//                        output = output.replaceRange(newTextFieldValueState.selection.start - addedCount,
//                            newTextFieldValueState.selection.start,
//                            newTextFieldValueState.text.substring(
//                                newTextFieldValueState.selection.start - addedCount, newTextFieldValueState.selection.start
//                            ))
//
////                        input[newTextFieldValueState.]
//                    }
//                    else if(newTextFieldValueState.text.length < lastTextValue.length) {
//                        val deletedCount = lastTextValue.length - newTextFieldValueState.text.length
//                        output = output.replaceRange(newTextFieldValueState.selection.start,
//                            newTextFieldValueState.selection.start + deletedCount,
//                            "0".repeat(deletedCount))
//                    }
//
//
//                    val year = (
//                            if(output.length >= 4) output.substring(0, 4).toInt()
//                            else if(output.isNotEmpty()) output.toInt()
//                            else 1
//                            ).coerceIn(1, 9999)
//
//                    val month = (
//                            if(output.length > 4) output.substring(4, if(output.length < 6) output.length else 6).toInt()
//                            else 1
//                            ).coerceIn(1, 12)
//
//                    val day = (
//                            if(output.length > 6) output.substring(6).toInt()
//                            else 1
//                            ).coerceIn(1, YearMonth.of(year, month).lengthOfMonth())
//
//                    try {
//                        val date = LocalDate(year, month, day)
//                        onDateChange(date)
//                    } catch (_: DateTimeException) {}
//
//
//                    textFieldValueState = newTextFieldValueState.copy(text = output)
//                }
//                else {
//                    textFieldValueState = newTextFieldValueState
//                }
//            }
//            else {
//                textFieldValueState = newTextFieldValueState
//            }
//
//            lastTextValue = newTextFieldValueState.text
//        },
//        visualTransformation = DateVisualTransformation()
//    )
//}

@Composable
fun InlineDateInput(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    readOnly: Boolean = false,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    fun dateFilter(text: AnnotatedString): TransformedText {

        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if(i == 4 || i == 6) out += "-"
            out += trimmed[i]
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                when(offset) {
                    0 -> 0
                    1 -> 1
                    2 -> 2
                    3 -> 3
                    4 -> 4
                    5 -> 6
                    6 -> 7
                    7 -> 9
                    8 -> 10
                    else -> throw NotImplementedError()
                }

            override fun transformedToOriginal(offset: Int): Int =
                when(offset) {
                    10 -> 8
                    9 -> 7
                    8 -> 6
                    7 -> 6
                    6 -> 5
                    5 -> 4
                    4 -> 4
                    3 -> 3
                    2 -> 2
                    1 -> 1
                    0 -> 0
                    else -> throw NotImplementedError()
                }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }

    class DateTransformation() : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            return dateFilter(text)
        }
    }


    var text by remember(date) {
        mutableStateOf(
            "%04d".format(date.year) + "%02d".format(date.monthNumber) + "%02d".format(date.dayOfMonth)
        )
    }

    InlineTextField(
        value = text,
        onValueChange = {
            if(it.length <= 8) {
                text = it

                if(text.length == 8)
                {
                    val year = text.substring(0, 4).toInt()
                    val month = text.substring(4, 6).toInt()
                    val day = text.substring(6).toInt()

                    if(
                        year >= 1 && year <= 9999
                        && month >= 1 && month <= 12
                        && day >= 1 && day <= YearMonth.of(year, month).lengthOfMonth()
                        ) {

                        val date = LocalDate(year, month, day)
                        onDateChange(date)
                    }
                }
            }
        },
        readOnly = readOnly,
        onDone = onDone,
        visualTransformation = DateTransformation(),
        modifier = modifier
    )
}