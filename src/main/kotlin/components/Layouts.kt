package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun MediaDetailsLayout(
    header: @Composable () -> Unit,
    poster: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    spacing: Dp = 20.dp
) {
    val localDensity = LocalDensity.current

    val policy = object : MeasurePolicy {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            val padding = with(localDensity) { spacing.roundToPx()}

            val headerPlaceable = measurables[0].measure(constraints)
            val posterPlaceable = measurables[1].measure(constraints)
            val rightContentPlaceable = measurables[2].measure(constraints.copy(maxWidth = if(constraints.hasBoundedWidth) constraints.maxWidth - posterPlaceable.width - padding * 3 else constraints.maxWidth))
            val bottomContentPlaceable = measurables[3].measure(constraints.copy(maxWidth = if(constraints.hasBoundedWidth)  constraints.maxWidth - padding * 2 else constraints.maxWidth))

            val posterYOffset = headerPlaceable.height / 3 * 2
            val posterBottomY = posterYOffset + posterPlaceable.height
            val rightContentOffsetX = posterPlaceable.width + 2 * padding
            val rightContentBottomY = headerPlaceable.height + rightContentPlaceable.height
            val bottomContentYOffset = max(posterBottomY, rightContentBottomY)

            val width = max(
                headerPlaceable.width,
                max(
                    rightContentOffsetX + rightContentPlaceable.width + padding,
                    bottomContentPlaceable.width + 2 * padding
                )
            )

            val height = bottomContentYOffset + bottomContentPlaceable.height

            return layout(
                width = width,
                height = height
            ) {
                headerPlaceable.place(0, 0)
                posterPlaceable.place(padding, posterYOffset, 2f)
                rightContentPlaceable.place(rightContentOffsetX, headerPlaceable.height)
                bottomContentPlaceable.place(padding, bottomContentYOffset)
            }
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