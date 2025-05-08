package components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
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

        for (originalSource in sources) {
            val focusRequester = remember { FocusRequester() }

            var source by remember { mutableStateOf(originalSource) }

            var editable by remember { mutableStateOf(false) }

            Source(
                name = source.name,
                type = source.type,
                url = source.url,
                focusRequester = focusRequester,
                editable = editable,
                nameWidth = nameWidth.dp,
                typeWidth = typeWidth.dp,
                onNameChange = { name -> source = source.copy(name = name) },
                onTypeChange = { type -> source = source.copy(type = type) },
                onUrlChange = { url -> source = source.copy(url = url) },
                onCommit = {},
                modifier = if(editable) Modifier.padding(start = 10.dp) else Modifier.padding(horizontal = 10.dp)
            ) {
                if(editable) {
                    TagIcon(
                        onClick = {
                            editable = false
                            source = originalSource
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                    }

                    InlineIconButton(
                        onClick = { editable = false }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
                else {
                    TagIcon(
                        onClick = { editable = true }
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
fun Source(
    editable: Boolean,
    name: String,
    nameWidth: Dp?,
    type: SourceType,
    typeWidth: Dp?,
    url: String,
    focusRequester: FocusRequester,
    onNameChange: (String) -> Unit,
    onTypeChange: (SourceType) -> Unit,
    onUrlChange: (String) -> Unit,
    onCommit: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current


    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SourceRow(modifier) {
            if(editable) {
                InlineTextField(
                    value = name,
                    onValueChange = onNameChange,
                    onDone = { focusManager.moveFocus(FocusDirection.Right) },
                    placeholder = "Name",
                    alignment = Alignment.CenterHorizontally,
                    modifier = nameWidth?.let { Modifier.requiredWidth(it) } ?: Modifier.weight(2f)
                        .focusRequester(focusRequester)
                )

                InlineDropdown(
                    options = SourceType.entries.map { it.name },
                    selectedIdx = type.ordinal,
                    onSelectedIdxChanged = { onTypeChange(SourceType.entries[it]) },
                )

                InlineTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    onDone = onCommit,
                    placeholder = "Url",
                    alignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(5f)
                )
            } else {
                SourceLabel(
                    text = name,
                    modifier = nameWidth?.let { Modifier.width(it) } ?: Modifier.weight(2f)
                )

                SourceLabel(
                    text = type.name,
                    modifier = typeWidth?.let { Modifier.width(it) } ?: Modifier.weight(1f)
                )

                SourceLabel(
                    text = url,
                    modifier = Modifier.weight(5f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                actions()
            }
        }
    }
}