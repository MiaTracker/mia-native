package views

import SettingsNavigation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import infrastructure.ErrorHandler
import view_models.SettingsUsersUiState
import view_models.SettingsUsersViewModel

@Composable
fun SettingsUsersView(
    navController: NavHostController,
    drawerState: DrawerState,
    errorHandler: ErrorHandler,
    viewModel: SettingsUsersViewModel = viewModel {
        SettingsUsersViewModel(
            errorHandler = errorHandler
        )
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsNavigation(
        navController = navController,
        drawerState = drawerState,
        errorHandler = errorHandler
    ) {
        when (val state = uiState) {
            is SettingsUsersUiState.Loading -> { }
            is SettingsUsersUiState.Loaded -> {
                val fontFamilyResolver = LocalFontFamilyResolver.current
                val density = LocalDensity.current
                val layoutDirection = LocalLayoutDirection.current

                val measurer = remember {
                    TextMeasurer(
                        defaultFontFamilyResolver = fontFamilyResolver,
                        defaultDensity = density,
                        defaultLayoutDirection = layoutDirection
                    )
                }

                val adminLabel = "Admin"
                val adminLabelWidth = with(LocalDensity.current) {
                    measurer.measure(text = adminLabel, style = MaterialTheme.typography.titleMedium).size.width.toDp()
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp, vertical = 15.dp),
                        ) {
                            Text(
                                text = "Username",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                            )

                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(2f),
                            )

                            Text(
                                text = adminLabel,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.width(adminLabelWidth),
                            )

                            Box(
                                modifier = Modifier.weight(1f),
                            )
                        }

                        for (user in state.users) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                            ) {
                                Text(
                                    text = user.username,
                                    modifier = Modifier.weight(1f),
                                )

                                Text(
                                    text = user.email,
                                    modifier = Modifier.weight(2f),
                                )

                                Icon(
                                    imageVector = if(user.admin) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.width(adminLabelWidth),
                                )

                                Box(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    IconButton(
                                        onClick = { viewModel.deleteUser(user) },
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .pointerHoverIcon(PointerIcon.Hand)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = viewModel::openUserRegistrationDialog,
                        modifier = Modifier
                            .align(Alignment.End)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Add, null)
                            Text("Register User")
                        }
                    }
                }
                
                if(state.userRegistrationDialogState != null) {
                    UserRegistrationDialog(
                        state = state.userRegistrationDialogState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun UserRegistrationDialog(
    state: SettingsUsersUiState.Loaded.UserRegistrationDialogState,
    viewModel: SettingsUsersViewModel
) {
    Dialog(
        onDismissRequest = viewModel::closeUserRegistrationDialog
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
                    text = "Registration",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(10.dp)
                )

                TextField(
                    value = state.username ?: "",
                    onValueChange = viewModel::setUsername,
                    label = { Text("Username") },
                    isError = state.username != null && state.username.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                TextField(
                    value = state.email ?: "",
                    onValueChange = viewModel::setEmail,
                    label = { Text("Email") },
                    isError = state.email != null && state.email.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                TextField(
                    value = state.password ?: "",
                    onValueChange = viewModel::setPassword,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = (state.password != null && state.password.isBlank()) || !state.passwordMatchesCriteria,
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        if(!state.passwordMatchesCriteria) {
                            Text(
                                text = "The password must be at least 7 characters in length and contain at least one digit, one upper case and one lowercase letter."
                            )
                        }
                    }
                )

                TextField(
                    value = state.passwordRepeat ?: "",
                    onValueChange = viewModel::setPasswordRepeat,
                    label = { Text("Repeat password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = state.passwordRepeat != null && (state.passwordRepeat.isBlank() || state.passwordRepeat != state.password),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    TextButton(
                        onClick = viewModel::closeUserRegistrationDialog,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = viewModel::registerUser,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
}