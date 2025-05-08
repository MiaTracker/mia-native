package components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import data_objects.Source
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
        modifier = modifier,
    ) {

        if(sources.isNotEmpty()) {
            SourceRow {
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
                shape = MaterialTheme.shapes.small,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                SourceRow {
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
fun SourceRow(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp, 2.dp)
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