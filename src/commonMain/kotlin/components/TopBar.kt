package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TopBar(
    navigationIcon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    searchbar: (@Composable () -> Unit)? = null,
) = Layout(
    content = {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val showTitle = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        Box {
            navigationIcon()
        }

        if(showTitle) {
            Box(
                Modifier
                    .padding(start = 10.dp)
            ) {
                val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.titleLarge)

                CompositionLocalProvider(
                    value = LocalTextStyle provides mergedStyle
                ) {
                    title()
                }
            }
        }
        else {
            Box(Modifier)
        }

        Box {
            if(searchbar != null) searchbar()
        }
    },
    modifier = Modifier
        .background(MaterialTheme.colorScheme.primaryContainer)
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(5.dp)
) { measurables, constraints ->


    val navButton = measurables[0].measure(
        Constraints(
            minWidth = 0,
            maxWidth = measurables[0].minIntrinsicWidth(constraints.maxHeight),
            minHeight = 0,
            maxHeight = measurables[0].minIntrinsicHeight(constraints.maxWidth)
        )
    )
    val title = measurables[1].measure(
        Constraints(
            minWidth = 0,
            maxWidth = measurables[1].maxIntrinsicWidth(constraints.maxHeight),
            minHeight = 0,
            maxHeight = measurables[1].minIntrinsicHeight(constraints.maxWidth)
        )
    )

    val height = max(navButton.height, title.height)


    val searchBar = measurables[2].measure(
        Constraints(
            minWidth = 0,
            maxWidth = min(
                (constraints.maxWidth * 0.7).roundToInt(),
                constraints.maxWidth - 2 * (navButton.width + title.width + 10.dp.roundToPx())
            ),
            minHeight = 0,
            maxHeight = height
        )
    )

    layout(constraints.maxWidth,height) {
        navButton.place(0, height / 2 - navButton.height / 2)
        title.place(navButton.width, height / 2 - title.height / 2)
        searchBar.place(constraints.maxWidth / 2 - searchBar.width / 2, height / 2 - searchBar.height / 2)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCommit: () -> Unit,
    queryValid: Boolean
) = Surface(
    color = if(queryValid) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.errorContainer,
    modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(2.dp)
) {

    BasicTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
        maxLines = 1,
        keyboardActions = KeyboardActions(
            onSearch = { onCommit() },
            onDone = { onCommit() },
            onGo = { onCommit() }
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    innerTextField()
                }

                if(searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(20.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable {
                                onSearchQueryChange("")
                            }
                    )
                }
            }
        }
    )
}
