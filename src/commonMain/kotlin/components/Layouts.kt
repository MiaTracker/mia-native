package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlin.math.max

@Composable
fun MediaDetailsLayout(
    header: @Composable () -> Unit,
    poster: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    spacing: Dp = 10.dp,
    padding: Dp = 20.dp
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if(windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
        ExpandedMediaDetailsLayout(
            header = header,
            poster = poster,
            rightContent = rightContent,
            bottomContent = bottomContent,
            spacing = spacing,
            padding = padding
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            Box {
                header()
                Box(
                    modifier = Modifier
                        .padding(start = padding)
                        .align(Alignment.BottomStart)
                ) {
                    poster()
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.padding(start = padding, end = padding, bottom = padding)
            ) {
                rightContent()
                bottomContent()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyFlowRow(
    width: Dp,
    spacing: Dp = 15.dp,
    modifier: Modifier = Modifier,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.FixedSize(size = width),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(spacing),
        modifier = modifier
            .fillMaxWidth()
    ) {
        content()
    }
}

@Composable
private fun ExpandedMediaDetailsLayout(
    header: @Composable () -> Unit,
    poster: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    spacing: Dp,
    padding: Dp
) {
    val localDensity = LocalDensity.current

    val policy = MeasurePolicy { measurables, constraints ->
        val spacing = with(localDensity) { spacing.roundToPx()}
        val padding = with(localDensity) { padding.roundToPx()}

        val headerPlaceable = measurables[0].measure(constraints)
        val posterPlaceable = measurables[1].measure(constraints)
        val rightContentPlaceable = measurables[2].measure(constraints.copy(maxWidth = if(constraints.hasBoundedWidth) constraints.maxWidth - posterPlaceable.width - padding * 3 else constraints.maxWidth))
        val bottomContentPlaceable = measurables[3].measure(constraints.copy(maxWidth = if(constraints.hasBoundedWidth)  constraints.maxWidth - padding * 2 else constraints.maxWidth))

        val posterYOffset = headerPlaceable.height / 3 * 2
        val posterBottomY = posterYOffset + posterPlaceable.height
        val rightContentOffsetX = posterPlaceable.width + 2 * padding
        val rightContentOffsetY = headerPlaceable.height + spacing
        val rightContentBottomY = rightContentOffsetY + rightContentPlaceable.height
        val bottomContentYOffset = max(posterBottomY, rightContentBottomY) + spacing

        val width = max(
            headerPlaceable.width,
            max(
                rightContentOffsetX + rightContentPlaceable.width + padding,
                bottomContentPlaceable.width + 2 * padding
            )
        )

        val height = bottomContentYOffset + bottomContentPlaceable.height + padding

        layout(
            width = width,
            height = height
        ) {
            headerPlaceable.place(0, 0)
            posterPlaceable.place(padding, posterYOffset, 2f)
            rightContentPlaceable.place(rightContentOffsetX, rightContentOffsetY)
            bottomContentPlaceable.place(padding, bottomContentYOffset)
        }
    }

    Layout(
        content = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                header()
            }
            Box {
                poster()
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                rightContent()
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                bottomContent()
            }
        },
        measurePolicy = policy
    )
}

@Composable
fun JumpUnderLayout(
    left: @Composable () -> Unit,
    middle: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    val policy = MeasurePolicy { measurables, constraints ->
        val leftPlaceable = measurables[0].measure(constraints)
        val rightPlaceable = measurables[2].measure(constraints)

        val middleWidth = measurables[1].maxIntrinsicWidth(constraints.maxHeight)

        if(leftPlaceable.width + rightPlaceable.width + middleWidth > constraints.maxWidth) {
            println("in 1")
            println(middleWidth)
            println(constraints.maxWidth)
            val middlePlaceable = measurables[1].measure(constraints.copy(
                maxWidth =
                    if(constraints.hasBoundedWidth)
                        constraints.maxWidth - leftPlaceable.width - rightPlaceable.width
                    else constraints.maxWidth
            ))

            layout(
                width = leftPlaceable.width + rightPlaceable.width + middlePlaceable.width,
                height = maxOf(leftPlaceable.height, middlePlaceable.height, rightPlaceable.height)
            ) {
                leftPlaceable.place(0, 0)
                middlePlaceable.place(leftPlaceable.width, 0)
                rightPlaceable.place(leftPlaceable.width + middlePlaceable.width, 0)
            }
        } else {
            println("in 2")
            val firstRowHeight = maxOf(leftPlaceable.height, rightPlaceable.height)
            val middlePlaceable = measurables[1].measure(constraints.copy(
                maxWidth = constraints.maxWidth,
                maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight - firstRowHeight else constraints.maxHeight
            ))

            val width = maxOf(leftPlaceable.width + rightPlaceable.width, middlePlaceable.width)
            layout(
                width = width,
                height = firstRowHeight + middlePlaceable.height
            ) {
                leftPlaceable.place(0, 0)
                rightPlaceable.place(width - rightPlaceable.width, 0)
                middlePlaceable.place(0, firstRowHeight)
            }
        }
    }

    Layout(
        content = {
            Row {
                left()
            }
            Row {
                middle()
            }
            Row {
                right()
            }
        },
        measurePolicy = policy
    )
}