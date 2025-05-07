package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagsRow(modifier: Modifier = Modifier, onAdd: (String) -> Unit, tags: @Composable RowScope.() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        tags()

        var adding by remember { mutableStateOf(false) }

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .onFocusChanged { state ->
                    if(!state.hasFocus) {
                        adding = false
                    }
                }
        ) {
            var tagName by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                if(adding) {
                    val focusRequester = remember { FocusRequester() }

                    BasicTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor),
                        textStyle = LocalTextStyle.current.copy(color = TextFieldDefaults.colors().focusedTextColor),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(10.dp, 2.dp)
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }

                Box(
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .onClick {
                            if(adding) onAdd(tagName)
                            else adding = true
                        }
                ) {

                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(2.dp))
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
            actions()
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
            .onClick {
                onClick()
            }
    ) {
        icon()
    }
}