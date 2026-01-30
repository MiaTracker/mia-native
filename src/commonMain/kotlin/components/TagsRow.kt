package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TagsRow(modifier: Modifier = Modifier, onAdd: (String) -> Unit, tags: @Composable RowScope.() -> Unit) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        tags()

        var adding by remember { mutableStateOf(false) }
        var tagName by remember { mutableStateOf("") }
        var focusRequested by remember { mutableStateOf(false) }

        fun clearTagName() {
            adding = false
            focusRequested = false
            tagName = ""
        }

        fun commitTag() {
            if(!tagName.isBlank()) onAdd(tagName)
            clearTagName()
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            if(adding) {
                val focusRequester = remember { FocusRequester() }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .onFocusChanged { state ->
                            if(focusRequested && !state.hasFocus) {
                                clearTagName()
                            }
                        }
                        .focusTarget()
                ) {

                    InlineTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        onDone = { commitTag() },
                        outline = false,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .widthIn(min = 100.dp)
                            .width(IntrinsicSize.Min)
                            .padding(horizontal = 10.dp)
                    )

                    InlineIconButton(onClick = {
                        commitTag()
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }
                LaunchedEffect(Unit) {
                    focusRequested = true
                    focusRequester.requestFocus()
                }
            } else {
                InlineIconButton(onClick = {
                    adding = true
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun Tag(actions: @Composable () -> Unit, label: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(10.dp, 2.dp)
        ) {
            label()

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                actions()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagIcon(onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .height(17.dp)
            .pointerHoverIcon(icon = PointerIcon.Hand)
            .clickable {
                onClick()
            }
    ) {
        icon()
    }
}