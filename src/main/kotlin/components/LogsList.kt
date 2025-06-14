package components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data_objects.Log
import data_objects.Source
import enums.SourceType
import helpers.append
import helpers.max
import helpers.toStarsString
import kotlinx.datetime.LocalDate

@Composable
fun LogsList(logs: List<Log>, sources: List<Source>, onUpdate: (Log) -> Unit, onDelete: (Log) -> Unit, modifier: Modifier = Modifier) {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val starsLabel = "Stars"
    val sourceLabel = "Source"
    val completedLabel = "Completed"
    val commentLabel = "Comment"
    val dateLabel = "Date"
    val dateLabelWidth = remember { textMeasurer.measure(dateLabel, textStyle).size.width }
//    val typeWidth = remember {
//        with(density) {
//            SourceType.entries.stream().map { textMeasurer.measure(it.name, textStyle).size.width }
//                .append(textMeasurer.measure(typeLabel, textStyle).size.width).max()?.toDp() ?: 0.dp
//        } + 10.dp
//    }
//
//    val nameWidth = with(density) {
//        sources.stream().map { textMeasurer.measure(it.name, textStyle).size.width }
//            .append(nameLabelWidth).max()?.toDp() ?: 0.dp
//    }


    val sourceNames = sources.map { it.name }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {

        if(logs.isNotEmpty()) {
            SourceRow(
                modifier = Modifier.padding(horizontal = 11.dp)
            ) {

                SourceLabel(
                    text = starsLabel,
                    modifier = Modifier
//                        .width(typeWidth)
                )

                SourceLabel(
                    text = sourceLabel,
                    modifier = Modifier
//                        .width(nameWidth)
                )
                SourceLabel(
                    text = completedLabel,
                    modifier = Modifier
//                        .width(typeWidth)
                )

                SourceLabel(
                    text = commentLabel,
                    modifier = Modifier.weight(2f)
                )


                SourceLabel(
                    text = dateLabel,
                    modifier = Modifier
//                        .width(typeWidth)
                )

                Spacer(
                    modifier = Modifier
                        .width((5 + 2 * 17).dp)
                )
            }
        }

        for (log in logs) {
            var editable by remember { mutableStateOf(false) }

            var sourceId by remember { mutableStateOf(sourceNames.indexOf(log.source)) }
            var stars by remember { mutableStateOf(log.stars) }
            var completed by remember { mutableStateOf(log.completed) }
            var comment by remember { mutableStateOf(log.comment) }
            var date by remember { mutableStateOf(log.date) }

            fun clear() {
                sourceId = sourceNames.indexOf(log.source)
                stars = log.stars
                completed = log.completed
                comment = log.comment
                date = log.date
                editable = false
            }

            fun commit() {
                if(!editable) return
                onUpdate(Log(id = log.id, date = date, source = sourceNames[sourceId], stars = stars, completed = completed, comment = comment))
                clear()
            }

            LogTag(
                editable = editable,
                sources = sourceNames,
                sourceId = sourceId,
                onSourceIdChange = { sourceId = it },
                stars = stars,
                onStarsChange = { stars = it },
                completed = completed,
                onCompletedChange = { completed = it },
                comment = comment,
                onCommentChange = { comment = it },
                date = date,
                onDateChange = { date = it },
                sourceWidth = 50.dp,
                starsWidth = 50.dp,
                completedWidth = 50.dp,
                dateWidth = 100.dp,
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
                        onClick = { onDelete(log) }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun LogTag(
    editable: Boolean,
    sources: List<String>,
    sourceId: Int,
    onSourceIdChange: (Int) -> Unit,
    stars: Float?,
    onStarsChange: (Float?) -> Unit,
    completed: Boolean,
    onCompletedChange: (Boolean) -> Unit,
    comment: String?,
    onCommentChange: (String?) -> Unit,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    sourceWidth: Dp,
    starsWidth: Dp,
    completedWidth: Dp,
    dateWidth: Dp,
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

            var starsText by remember { mutableStateOf(stars?.toStarsString() ?: "") }


            InlineTextField(
                value = starsText,
                onValueChange = {
                    if(it.isBlank()) starsText = ""
                    else {
                        val float = it.toFloatOrNull()
                        if(float != null && float >= 0f && float <= 10f) {
                            starsText = it
                            onStarsChange(float)
                        }
                    }
                },
                readOnly = !editable,
                alignment = Alignment.CenterHorizontally,
                placeholder = "Stars",
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(starsWidth)
            )

            InlineDropdown(
                options = sources,
                selectedIdx = sourceId,
                onSelectedIdxChanged = onSourceIdChange,
                readOnly = !editable
            )

            InlineCheckbox(
                value = completed,
                onValueChange = onCompletedChange,
                readOnly = !editable,
                modifier = Modifier
            )

            InlineTextField(
                value = comment ?: "",
                onValueChange = onCommentChange,
                readOnly = !editable,
                alignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(2f)
            )

            InlineDateInput(
                date = date,
                onDateChange = onDateChange,
            );

            InlineTextField(
                value = date.toString(),
                onValueChange = { onDateChange(date) },
                readOnly = !editable,
                alignment = Alignment.CenterHorizontally,
                placeholder = "Date",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(dateWidth)
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