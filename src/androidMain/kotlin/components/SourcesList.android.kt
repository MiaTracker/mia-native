package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun SourceRow(
    modifier: Modifier,
    leftFields: @Composable () -> Unit,
    mainField: @Composable () -> Unit,
    rightFields: @Composable () -> Unit,
    actions: List<Action>
) = Box {
    var sheetShown by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    SourceRow(
        modifier = modifier,
        leftFields = leftFields,
        mainField = mainField,
        rightFields = rightFields,
    )

    if(sheetShown) {
        ModalBottomSheet(
            onDismissRequest = { sheetShown = false },
            sheetState = sheetState
        ) {
            Column {
                actions.forEach { action ->
                    ListItem(
                        headlineContent = { Text(action.name) },
                        leadingContent = { action.icon },
                        modifier = Modifier.clickable {
                            sheetShown = false
                            action.callback()
                        }
                    )
                }
            }
        }
    }
}

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
    rightFields = rightFields,
)

@Composable fun SourceRow(
    modifier: Modifier,
    leftFields: @Composable () -> Unit,
    mainField: @Composable () -> Unit,
    rightFields: @Composable () -> Unit,
) = JumpUnderLayout(
    left = leftFields,
    middle = mainField,
    right = rightFields
)