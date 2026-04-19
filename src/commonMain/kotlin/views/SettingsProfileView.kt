package views

import SettingsNavigation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import extensions.formFieldKeyEvents
import infrastructure.ErrorHandler
import view_models.SettingsProfileUiState
import view_models.SettingsProfileViewModel

@Composable
fun SettingsProfileView(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    viewModel: SettingsProfileViewModel = viewModel {
        SettingsProfileViewModel(
            errorHandler = errorHandler
        )
    },
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState
    if(state !is SettingsProfileUiState.Loaded) return

    SettingsNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            TextField(
                value = state.profile.username,
                onValueChange = {},
                label = { Text("Username") },
                enabled = false
            )

            TextField(
                value = state.profile.email,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.profile.admin,
                    onCheckedChange = {},
                    enabled = false,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                )

                Text(
                    text = "Admin",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            OutlinedButton(
                onClick = viewModel::openChangePasswordDialog,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Lock, null)
                    Text("Change Password")
                }
            }
        }

        if(state.changePasswordDialogState != null) {
            PasswordChangeDialog(
                state = state.changePasswordDialogState,
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun PasswordChangeDialog(
    state: SettingsProfileUiState.Loaded.ChangePasswordDialogState,
    viewModel: SettingsProfileViewModel,
) {
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = viewModel::closeChangePasswordDialog
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(10.dp),
            ) {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(10.dp)
                )

                TextField(
                    value = state.oldPassword ?: "",
                    onValueChange = viewModel::setOldPassword,
                    label = { Text("Old Password") },
                    isError = state.oldPassword == "" || !state.oldPasswordCorrect,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                        .formFieldKeyEvents(focusManager) { focusManager.moveFocus(FocusDirection.Next) }
                )

                TextField(
                    value = state.newPassword ?: "",
                    onValueChange = viewModel::setNewPassword,
                    label = { Text("New Password") },
                    isError = state.newPassword == "" || !state.newPasswordMatchesCriteria,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                        .formFieldKeyEvents(focusManager) { focusManager.moveFocus(FocusDirection.Next) },
                    supportingText = {
                        if(!state.newPasswordMatchesCriteria) {
                            Text(
                                text = "The password must be at least 7 characters in length and contain at least one digit, one upper case and one lowercase letter."
                            )
                        }
                    }
                )

                TextField(
                    value = state.repeatPassword ?: "",
                    onValueChange = viewModel::setRepeatPassword,
                    label = { Text("Repeat New Password") },
                    isError = (state.repeatPassword != state.newPassword || state.repeatPassword == "") && state.repeatPassword != null,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                        .formFieldKeyEvents(focusManager, viewModel::changePassword),
                    supportingText = {
                        if((state.repeatPassword != state.newPassword || state.repeatPassword == "") && state.repeatPassword != null) {
                            Text(
                                text = "Passwords do not match",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    TextButton(
                        onClick = viewModel::closeChangePasswordDialog,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = viewModel::changePassword,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Change Password")
                    }
                }
            }
        }
    }
}