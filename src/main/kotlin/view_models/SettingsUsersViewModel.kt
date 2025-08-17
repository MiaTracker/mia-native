package view_models

import Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.Result
import data_objects.UserProfile
import data_objects.UserRegister
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SettingsUsersUiState {
    object Loading : SettingsUsersUiState
    data class Loaded(
        val users: List<UserProfile>,
        val userRegistrationDialogState: UserRegistrationDialogState? = null
    ) : SettingsUsersUiState {
        data class UserRegistrationDialogState(
            val username: String? = null,
            val email: String? = null,
            val password: String? = null,
            val passwordRepeat: String? = null,
        ) {
            val passwordRegex: Regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{7,}\$".toRegex()

            val passwordMatchesCriteria = password?.let { password -> passwordRegex.matches(password) } ?: true
        }
    }
}

class SettingsUsersViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsUsersUiState> = MutableStateFlow(SettingsUsersUiState.Loading)
    val uiState: StateFlow<SettingsUsersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun deleteUser(user: UserProfile) {
        viewModelScope.launch {
            val result = Api.instance.Users().delete(user.uuid)

            when(result) {
                is Result.Error<*> -> TODO()
                is Result.Success<Unit> -> {
                    load()
                }
            }
        }
    }

    fun openUserRegistrationDialog() {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        _uiState.value = state.copy(userRegistrationDialogState = SettingsUsersUiState.Loaded.UserRegistrationDialogState())
    }

    fun closeUserRegistrationDialog() {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        _uiState.value = state.copy(userRegistrationDialogState = null)
    }

    fun setUsername(username: String) {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        val dialogState = state.userRegistrationDialogState ?: return

        _uiState.value = state.copy(userRegistrationDialogState = dialogState.copy(username = username))
    }

    fun setEmail(email: String) {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        val dialogState = state.userRegistrationDialogState ?: return

        _uiState.value = state.copy(userRegistrationDialogState = dialogState.copy(email = email))
    }

    fun setPassword(password: String) {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        val dialogState = state.userRegistrationDialogState ?: return

        _uiState.value = state.copy(userRegistrationDialogState = dialogState.copy(password = password))
    }

    fun setPasswordRepeat(passwordRepeat: String) {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        val dialogState = state.userRegistrationDialogState ?: return

        _uiState.value = state.copy(userRegistrationDialogState = dialogState.copy(passwordRepeat = passwordRepeat))
    }

    fun registerUser() {
        val state = _uiState.value
        if (state !is SettingsUsersUiState.Loaded) return
        val dialogState = state.userRegistrationDialogState ?: return

        if(dialogState.email.isNullOrBlank() || dialogState.username.isNullOrBlank()
            || dialogState.password.isNullOrBlank() || dialogState.passwordRepeat != dialogState.password
            || !dialogState.passwordMatchesCriteria) return

        viewModelScope.launch {
            val result = Api.instance.Users().register(
                UserRegister(
                    username = dialogState.username,
                    email = dialogState.email,
                    password = dialogState.password,
                    passwordRepeat = dialogState.passwordRepeat
                )
            )

            when(result) {
                is Result.Error<*> -> TODO()
                is Result.Success<*> -> {
                    load()
                    closeUserRegistrationDialog()
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val result = Api.instance.Users().index()

            when(result) {
                is Result.Error<*> -> TODO()
                is Result.Success<List<UserProfile>> -> {
                    _uiState.value = SettingsUsersUiState.Loaded(
                        users = result.value
                    )
                }
            }
        }
    }
}