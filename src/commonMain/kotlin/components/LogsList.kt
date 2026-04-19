package components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data_objects.Log
import data_objects.Source
import extensions.toStarsString
import kotlinx.datetime.LocalDate

@Composable
fun LogsList(logs: List<Log>, sources: List<Source>, onUpdate: (Log) -> Unit, onDelete: (Log) -> Unit, modifier: Modifier = Modifier, pendingItemId: Int? = null) {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()

    val starsLabel = "Stars"
    val sourceLabel = "Source"
    val completedLabel = "Completed"
    val commentLabel = "Comment"
    val dateLabel = "Date"
    val dateWidth = remember {
        maxOf(
            textMeasurer.measure(dateLabel, textStyle).size.width,
            textMeasurer.measure("8888-88-88", textStyle).size.width + 1
        )
    }

    val sourceNames = sources.map { it.name }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {

        if(logs.isNotEmpty()) {
            SourceRow(
                modifier = Modifier.padding(horizontal = 11.dp),
                leftFields = {
                    SourceLabel(
                        text = starsLabel,
                        modifier = Modifier
                    )

                    SourceLabel(
                        text = sourceLabel,
                        modifier = Modifier
                    )
                    SourceLabel(
                        text = completedLabel,
                        modifier = Modifier
                    )
                },
                mainField = {
                    SourceLabel(
                        text = commentLabel,
                        modifier = Modifier.weight(2f)
                    )
                },
                rightFields = {
                    SourceLabel(
                        text = dateLabel,
                        modifier = Modifier
                    )
                },
                actionPlaceholders = 2
            )
        }

        for (log in logs) {
            var editable by remember { mutableStateOf(false) }

            var sourceId by remember(log, sources) { mutableStateOf(sourceNames.indexOf(log.source)) }
            var stars by remember(log) { mutableStateOf(log.stars) }
            var completed by remember(log) { mutableStateOf(log.completed) }
            var comment by remember(log) { mutableStateOf(log.comment) }
            var date by remember(log) { mutableStateOf(log.date) }

            fun clear() {
                sourceId = sourceNames.indexOf(log.source)
                stars = log.stars
                completed = log.completed
                comment = log.comment
                date = log.date
                editable = false
            }

            fun commit() {
                if (!editable) return
                onUpdate(
                    Log(
                        id = log.id,
                        date = date,
                        source = sourceNames[sourceId],
                        stars = stars,
                        completed = completed,
                        comment = comment
                    )
                )
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
                starsWidth = 50.dp,
                dateWidth = dateWidth.dp,
                onCommit = { commit() },
                actions = if(pendingItemId == log.id) {
                    listOf(Action(
                        icon = { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) },
                        name = "",
                        filled = false,
                        callback = {}
                    ))
                } else sequence {
                    if (editable) {
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
                    } else {
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
                            callback = { onDelete(log) }
                        ))
                    }
                }.toList()
            )
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
    starsWidth: Dp,
    dateWidth: Dp,
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
                    placeholder = "#",
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
                    readOnly = !editable,
                )

                InlineCheckbox(
                    value = completed,
                    onValueChange = onCompletedChange,
                    readOnly = !editable,
                    modifier = Modifier
                )
            },
            mainField = {
                InlineTextField(
                    value = comment ?: "",
                    onValueChange = onCommentChange,
                    readOnly = !editable,
                    singleLine = false,
                    alignment = Alignment.CenterHorizontally,
                    placeholder = "Comment",
                    modifier = Modifier
                )
            },
            rightFields = {
                InlineDateInput(
                    date = date,
                    onDateChange = onDateChange,
                    readOnly = !editable,
                    onDone = onCommit,
                    modifier = Modifier
                        .width(dateWidth)
                )
            },
            actions = actions
        )
    }
}