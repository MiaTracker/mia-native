package views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import view_models.InstanceSelectionViewModel

@Composable
fun InstanceSelectionView(
    navController: NavHostController,
    viewModel: InstanceSelectionViewModel = viewModel { InstanceSelectionViewModel(navController) }
) {
    val state by viewModel.uiState.collectAsState()

    val focusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(700.dp)
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
                        Text(text = "Connect")
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}