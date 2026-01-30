package components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun SourceRow(
    modifier: Modifier,
    leftFields: @Composable () -> Unit,
    mainField: @Composable () -> Unit,
    rightFields: @Composable () -> Unit,
    actions: List<Action>
) = SourceRow(
    modifier = modifier,
    leftFields = leftFields,
    mainField = mainField,
    rightFields = {
        rightFields()

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            actions.forEach { action ->
                if(action.filled) {
                    InlineIconButton(
                        onClick = action.callback,
                    ) {
                        action.icon()
                    }
                } else {
                    TagIcon(
                        onClick = action.callback
                    ) {
                        action.icon()
                    }
                }
            }
        }
    }
)

@Composable
actual fun SourceRow(
    modifier: Modifier,
    leftFields: @Composable () -> Unit,
    mainField: @Composable () -> Unit,
    rightFields: @Composable () -> Unit,
    actionPlaceholders: Int
) = SourceRow(
    modifier = modifier,
    leftFields = leftFields,
    mainField = mainField,
    rightFields = {
        rightFields()
        Spacer(
            modifier = Modifier
                .width((5 * (actionPlaceholders - 1) + actionPlaceholders * 17).dp)
        )
    }
)
@Composable fun SourceRow(
    modifier: Modifier,
    leftFields: @Composable () -> Unit,
    mainField: @Composable () -> Unit,
    rightFields: @Composable () -> Unit,
) = JumpUnderLayout(
    left = {
        Row(
            modifier = Modifier.fillMaxHeight()
        ) {
            leftFields()
        }
    },
    middle = {
        Row(
            modifier = Modifier.fillMaxHeight()
        ) {
            mainField()
        }
    },
    right = {
        Row(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            rightFields()
        }
    },
    modifier = Modifier
)