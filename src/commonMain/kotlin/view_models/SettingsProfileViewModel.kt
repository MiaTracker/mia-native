package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.PasswordChange
import data_objects.Result
import data_objects.UserProfile
import infrastructure.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SettingsProfileUiState {
    object Loading : SettingsProfileUiState
    data class Loaded(
        val profile: UserProfile,

        val changePasswordDialogState: ChangePasswordDialogState? = null
    ) : SettingsProfileUiState {
        data class ChangePasswordDialogState(
            val oldPassword: String? = null,
            val newPassword: String? = null,
            val repeatPassword: String? = null,
            val oldPasswordCorrect: Boolean = true
        ) {
            val passwordRegex: Regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{7,}\$".toRegex()

            val newPasswordMatchesCriteria = newPassword?.let { password -> passwordRegex.matches(password) } ?: true
        }
    }
}

class SettingsProfileViewModel(
    private val errorHandler: ErrorHandler
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsProfileUiState> = MutableStateFlow(SettingsProfileUiState.Loading)
    val uiState: StateFlow<SettingsProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Unconfined) {
            when (val result = Api.instance.Users().profile()) {
                is Result.Error<*> -> with(errorHandler) { result.handle() }
                is Result.Success<UserProfile> -> {
                    _uiState.value = SettingsProfileUiState.Loaded(
                        profile = result.value
                    )
                }
            }
        }
    }

    fun setOldPassword(oldPassword: String) {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        if(state.changePasswordDialogState == null) return

        _uiState.value = state.copy(
            changePasswordDialogState = state.changePasswordDialogState.copy(
                oldPassword = oldPassword
            )
        )
    }

    fun setNewPassword(newPassword: String) {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        if(state.changePasswordDialogState == null) return

        _uiState.value = state.copy(
            changePasswordDialogState = state.changePasswordDialogState.copy(
                newPassword = newPassword
            )
        )
    }

    fun setRepeatPassword(repeatPassword: String) {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        if(state.changePasswordDialogState == null) return

        _uiState.value = state.copy(
            changePasswordDialogState = state.changePasswordDialogState.copy(
                repeatPassword = repeatPassword
            )
        )
    }

    fun openChangePasswordDialog() {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        _uiState.value = state.copy(
            changePasswordDialogState = SettingsProfileUiState.Loaded.ChangePasswordDialogState()
        )
    }

    fun closeChangePasswordDialog() {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        _uiState.value = state.copy(
            changePasswordDialogState = null
        )
    }

    fun changePassword() {
        val state = _uiState.value
        if(state !is SettingsProfileUiState.Loaded) return
        val dialogState = state.changePasswordDialogState ?: return

        if(dialogState.oldPassword.isNullOrEmpty() && dialogState.newPassword.isNullOrEmpty() || dialogState.newPassword != dialogState.repeatPassword) {
            _uiState.value = state.copy(
                changePasswordDialogState = dialogState.copy(
                    oldPassword = dialogState.oldPassword ?: "",
                    newPassword = dialogState.newPassword ?: "",
                    repeatPassword = dialogState.repeatPassword ?: ""
                )
            )
            return
        }

        viewModelScope.launch {
            val result = Api.instance.Users().changePassword(
                PasswordChange(
                    oldPassword = dialogState.oldPassword!!,
                    newPassword = dialogState.newPassword!!,
                    passwordRepeat = dialogState.repeatPassword!!
                )
            )

            when(result) {
                is Result.Error<*> -> with(errorHandler) { result.handle() }
                is Result.Success<*> -> {
                    _uiState.value = state.copy(
                        changePasswordDialogState = null
                    )
                }
            }
        }
    }
}