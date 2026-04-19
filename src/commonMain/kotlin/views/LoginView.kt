package views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.BaseScaffold
import extensions.formFieldKeyEvents
import infrastructure.ErrorHandler
import view_models.LoginUiState
import view_models.LoginViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    errorHandler: ErrorHandler,
    viewModel: LoginViewModel = viewModel {
        LoginViewModel(
            navController = navController,
            errorHandler = errorHandler
        )
    }
) {
    val vmState by viewModel.uiState.collectAsState()

    val state = vmState
    if(state !is LoginUiState.Loaded) return

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BaseScaffold(
        errorHandler = errorHandler
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 700.dp)
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                TextField(
                    value = state.username,
                    onValueChange = { viewModel.setUsername(it) },
                    label = { Text("Username") },
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                    },
                    singleLine = true,
                    isError = state.isLoginIncorrect,
                    enabled = !state.loggingIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .formFieldKeyEvents(focusManager) { focusManager.moveFocus(FocusDirection.Next); }
                )

                TextField(
                    value = state.password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.login() }
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    singleLine = true,
                    enabled = !state.loggingIn,
                    isError = state.isLoginIncorrect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .formFieldKeyEvents(focusManager) { viewModel.login(); }
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { viewModel.changeInstance() },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Change instance")
                    }

                    Button(
                        onClick = { viewModel.login() },
                        enabled = state.isValid && !state.loggingIn && !state.isLoginIncorrect,
                        modifier = Modifier
                            .pointerHoverIcon(if(state.isValid) PointerIcon.Hand else PointerIcon.Default)
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}