package views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.BaseScaffold
import components.LoadingSpinner
import infrastructure.ErrorHandler
import view_models.InstanceSelectionUiState
import view_models.InstanceSelectionViewModel

@Composable
fun InstanceSelectionView(
    navController: NavHostController,
    errorHandler: ErrorHandler,
    viewModel: InstanceSelectionViewModel = viewModel {
        InstanceSelectionViewModel(
            _navController = navController
        )
    }
) {
    val vmState by viewModel.uiState.collectAsState()

    val state = vmState
    if(state !is InstanceSelectionUiState.Loaded) {
        BaseScaffold(errorHandler = errorHandler, modifier = Modifier.padding(15.dp)) { LoadingSpinner() }
        return
    }

    val focusRequester = remember { FocusRequester() }

    BaseScaffold(
        errorHandler = errorHandler,
        modifier = Modifier
            .padding(15.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 700.dp)
            ) {
                Text(
                    text = "Instance selection",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                TextField(
                    value = state.url,
                    onValueChange = { viewModel.setUrl(it) },
                    label = { Text("Instance url") },
                    enabled = !state.connecting,
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.connect() },
                        onGo = { viewModel.connect() },
                        onSend = { viewModel.connect() },
                        onSearch = { viewModel.connect() },
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    },
                    isError = state.instanceExists == false,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .onKeyEvent { event ->
                            if(event.key == Key.Enter) {
                                if(event.type == KeyEventType.KeyDown)
                                    viewModel.connect()
                                true
                            } else false
                        },
                )


                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { viewModel.test() },
                        enabled = state.isValid && !state.connecting,
                        modifier = Modifier
                            .pointerHoverIcon(if (state.isValid && !state.connecting) PointerIcon.Hand else PointerIcon.Default)
                    ) {
                        Text(text = "Test connection")
                    }

                    Button(
                        onClick = { viewModel.connect() },
                        enabled = state.isValid && !state.connecting,
                        modifier = Modifier
                            .pointerHoverIcon(if (state.isValid && !state.connecting) PointerIcon.Hand else PointerIcon.Default)
                    ) {
                        if(state.connecting) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text(text = "Connect")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}