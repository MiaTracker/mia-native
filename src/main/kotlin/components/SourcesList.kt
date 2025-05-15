package components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data_objects.Source
import enums.SourceType
import helpers.append
import helpers.max

@Composable
fun SourcesList(sources: List<Source>, onUpdate: (Source) -> Unit, onDelete: (Source) -> Unit, modifier: Modifier = Modifier) {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val nameLabel = "Name"
    val typeLabel = "Type"
    val urlLabel = "Url"
    val nameLabelWidth = remember { textMeasurer.measure(nameLabel, textStyle).size.width }
    val typeWidth = remember {
        with(density) {
            SourceType.entries.stream().map { textMeasurer.measure(it.name, textStyle).size.width }
                .append(textMeasurer.measure(typeLabel, textStyle).size.width).max()?.toDp() ?: 0.dp
        } + 10.dp
    }

    val nameWidth = with(density) {
        sources.stream().map { textMeasurer.measure(it.name, textStyle).size.width }
                .append(nameLabelWidth).max()?.toDp() ?: 0.dp
    }


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {

        if(sources.isNotEmpty()) {
            SourceRow(
                modifier = Modifier.padding(horizontal = 11.dp)
            ) {
                SourceLabel(
                    text = nameLabel,
                    modifier = Modifier
                        .width(nameWidth)
                )

                SourceLabel(
                    text = typeLabel,
                    modifier = Modifier.width(typeWidth)
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
            var editable by remember { mutableStateOf(false) }

            var name by remember(source.name) { mutableStateOf(source.name) }
            var type by remember(source.type) { mutableStateOf(source.type) }
            var url by remember(source.url) { mutableStateOf(source.url) }

            fun clear() {
                name = source.name
                type = source.type
                url = source.url
                editable = false
            }

            fun commit() {
                if(!editable) return
                onUpdate(Source(source.id, name, url, type))
                clear()
            }

            SourceTag(
                editable = editable,
                name = name,
                onNameChange = { name = it },
                type = type,
                onTypeChange = { type = it },
                url = url,
                onUrlChange = { url = it },
                nameWidth = nameWidth,
                onCommit = { commit() }
            ) {
                if(editable) {
                    TagIcon(
                        onClick = { clear() }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                    }

                    InlineIconButton(
                        onClick = {
                            commit()
                        },
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
fun SourceTag(
    editable: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    type: SourceType,
    onTypeChange: (SourceType) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    nameWidth: Dp,
    onCommit: () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.small,
    ) {
        SourceRow(
            modifier =
                if(editable) Modifier.padding(start = 11.dp)
                    .fillMaxHeight()
                else Modifier.padding(horizontal = 11.dp)
        ) {

            InlineTextField(
                value = name,
                onValueChange = onNameChange,
                readOnly = !editable,
                alignment = Alignment.CenterHorizontally,
                placeholder = "Name",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(nameWidth)
            )

            InlineDropdown(
                options = SourceType.entries.map { it.name },
                selectedIdx = type.ordinal,
                onSelectedIdxChanged = { onTypeChange(SourceType.entries[it]) },
                readOnly = !editable
            )

            InlineTextField(
                value = url,
                onValueChange = onUrlChange,
                onDone = { onCommit() },
                readOnly = !editable,
                alignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(2f)
            )

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