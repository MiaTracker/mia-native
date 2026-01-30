package components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data_objects.Source
import enums.SourceType

private val UrlRegex = Regex("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")

data class Action(
    val icon: @Composable () -> Unit,
    val name: String,
    val filled: Boolean,
    val callback: () -> Unit
)

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
            maxOf(SourceType.entries.maxOfOrNull { textMeasurer.measure(it.name, textStyle).size.width } ?: 0,
                textMeasurer.measure(typeLabel, textStyle).size.width).toDp()
        } + 10.dp
    }

    val nameWidth = with(density) {
        maxOf(sources.maxOfOrNull { textMeasurer.measure(it.name, textStyle).size.width } ?: 0, nameLabelWidth).toDp()
    } + 10.dp


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {

        if(sources.isNotEmpty()) {
            SourceRow(
                modifier = Modifier.padding(horizontal = 11.dp),
                leftFields = {
                    SourceLabel(
                        text = nameLabel,
                        modifier = Modifier
                            .width(nameWidth)
                    )

                    SourceLabel(
                        text = typeLabel,
                        modifier = Modifier.width(typeWidth)
                    )
                },
                mainField = {
                    SourceLabel(
                        text = urlLabel,
                    )
                },
                rightFields = {},
                actionPlaceholders = if(sources.any { UrlRegex.matchEntire(it.url) != null }) 3 else 2
            )
        }

        val uriHandler = LocalUriHandler.current

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

            val isUrl = remember(url) {
                UrlRegex.matchEntire(url) != null
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
                onCommit = { commit() },
                actions = sequence {

                    if(editable) {
                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Cancel, contentDescription = null) },
                            name = "Cancel",
                            filled = false,
                            callback = ::clear
                        ))

                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) },
                            name = "Save",
                            filled = true,
                            callback = ::commit
                        ))
                    }
                    else {
                        if(isUrl) {
                            yield(Action(
                                icon = { Icon(imageVector = Icons.Default.Link, contentDescription = null) },
                                name = "Open Link",
                                filled = false,
                                callback = { uriHandler.openUri(url) }
                            ))
                        }

                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
                            name = "Edit",
                            filled = false,
                            callback = { editable = true }
                        ))

                        yield(Action(
                            icon = { Icon(imageVector = Icons.Default.Close, contentDescription = null) },
                            name = "Delete",
                            filled = false,
                            callback = { onDelete(source) }
                        ))
                    }
                }.toList()
            )
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
    actions: List<Action>
) {

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.small,
    ) {
        SourceRow(
            modifier =
                if(editable) Modifier.padding(start = 11.dp)
                    .fillMaxHeight()
                else Modifier.padding(horizontal = 11.dp),
            leftFields = {
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
            },
            mainField = {
                InlineTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    onDone = { onCommit() },
                    readOnly = !editable,
                    alignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                )
            },
            rightFields = {},
            actions = actions
        )
    }
}

@Composable
expect fun SourceRow(modifier: Modifier = Modifier, leftFields: @Composable () -> Unit,
                     mainField: @Composable () -> Unit, rightFields: @Composable () -> Unit,
                     actions: List<Action> = emptyList())

@Composable
expect fun SourceRow(modifier: Modifier = Modifier, leftFields: @Composable () -> Unit,
                     mainField: @Composable () -> Unit, rightFields: @Composable () -> Unit,
                     actionPlaceholders: Int = 0)

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