package views

import InnerNavigation
import Navigation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import view_models.SettingsProfileUiState
import view_models.SettingsProfileViewModel

@Composable
fun SettingsProfileView(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: SettingsProfileViewModel = viewModel { SettingsProfileViewModel() },
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState
    if(state !is SettingsProfileUiState.Loaded) return

    val backstack by navController.currentBackStackEntryAsState()

    InnerNavigation(
        navController = navController,
        drawerState = drawerState,
        title = { Text("Profile") }
    ) {

        PermanentNavigationDrawer(
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                ) {
                    Column {
                        NavigationDrawerItem(
                            label = { Text("Profile") },
                            icon = { Icon(Icons.Default.Person, null) },
                            selected = backstack?.destination?.hasRoute<Navigation.Inner.Settings.Profile>() == true,
                            onClick = {
                                navController.navigate(Navigation.Inner.Settings.Profile)
                            },
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                        )
                    }
                }
            }
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
}


@Composable
fun PasswordChangeDialog(
    state: SettingsProfileUiState.Loaded.ChangePasswordDialogState,
    viewModel: SettingsProfileViewModel,
) {
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
                )

                TextField(
                    value = state.newPassword ?: "",
                    onValueChange = viewModel::setNewPassword,
                    label = { Text("New Password") },
                    isError = state.newPassword == "" || !state.newPasswordMatchesCriteria,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
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