package components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import data_objects.Source
import enums.SourceType
import helpers.append
import helpers.max

@Composable
fun SourcesList(sources: List<Source>, onEdit: (Source) -> Unit, onDelete: (Source) -> Unit, modifier: Modifier = Modifier) {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()

    val nameLabel = "Name"
    val typeLabel = "Type"
    val urlLabel = "Url"
    val nameLabelWidth = remember { textMeasurer.measure(nameLabel, textStyle).size.width }
    val typeLabelWidth = remember { textMeasurer.measure(typeLabel, textStyle).size.width }

    val nameWidth = sources.stream().map { textMeasurer.measure(it.name, textStyle).size.width }
            .append(nameLabelWidth).max() ?: 0

    val typeWidth = sources.stream().map { textMeasurer.measure(it.type.name, textStyle).size.width }
        .append(typeLabelWidth).max() ?: 0

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {

        if(sources.isNotEmpty()) {
            SourceRow(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                SourceLabel(
                    text = nameLabel,
                    modifier = Modifier
                        .width(nameWidth.dp)
                )

                SourceLabel(
                    text = typeLabel,
                    modifier = Modifier.width(typeWidth.dp)
                )

                SourceLabel(
                    text = urlLabel,
                    modifier = Modifier.weight(2f)
                )

                Spacer(
                    modifier = Modifier
                        .width((5 + 2 * 17).dp)
                )
            }
        }

        for (source in sources) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.small
            ) {
                SourceRow(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    SourceLabel(
                        text = source.name,
                        modifier = Modifier
                            .width(nameWidth.dp)
                    )

                    SourceLabel(
                        text = source.type.name,
                        modifier = Modifier.width(typeWidth.dp)
                    )

                    SourceLabel(
                        text = source.url,
                        modifier = Modifier.weight(2f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        TagIcon(
                            onClick = { onEdit(source) }
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        }

                        TagIcon(
                            onClick = { onDelete(source) }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SourceRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxHeight()
    ) {
        content()
    }
}

@Composable
fun SourceLabel(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun SourceEditDialog(
    name: String,
    type: SourceType,
    url: String,
    onCancelRequest: () -> Unit,
    onSaveRequest: (name: String, type: SourceType, url: String) -> Unit
) {

    var name by remember { mutableStateOf(name) }
    var type by remember { mutableStateOf(type) }
    var url by remember { mutableStateOf(url) }

    var isTypeDropdownExpanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isValid = name.isNotBlank()

    EditDialog(
        onCancelRequest = onCancelRequest,
        onSaveRequest = {
            onSaveRequest(name, type, url)
        },
        title = "Edit source",
        isValid = isValid
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Name")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(2f)
                        .focusRequester(focusRequester)
                        .onKeyEvent { event ->
                            if(event.key == Key.Enter) {
                                if(event.type == KeyEventType.KeyDown)
                                    focusManager.moveFocus(FocusDirection.Right)
                                true
                            } else false
                        }
                )

                Box(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    TextField(
                        value = type.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Type") },
                        modifier = Modifier
                            .onFocusEvent { event ->
                                if(event.hasFocus) isTypeDropdownExpanded = true
                            }
                            .fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = isTypeDropdownExpanded,
                        onDismissRequest = {
                            isTypeDropdownExpanded = false
                        },
                        modifier = Modifier
                    ) {
                        SourceType.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item.name) },
                                onClick = {
                                    type = item
                                    isTypeDropdownExpanded = false
                                    focusManager.moveFocus(FocusDirection.Right)
                                }
                            )
                        }
                    }
                }
            }

            TextField(
                value = url,
                onValueChange = { url = it },
                label = {
                    Text("Url")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent { event ->
                        if(event.key == Key.Enter) {
                            if(event.type == KeyEventType.KeyDown && isValid)
                                onSaveRequest(name, type, url)
                            true
                        } else false
                    }
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}