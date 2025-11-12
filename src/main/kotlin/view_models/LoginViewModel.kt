package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import data_objects.Errors
import data_objects.LoginRequest
import data_objects.LoginResult
import data_objects.Result
import infrastructure.ErrorHandler
import infrastructure.Preferences
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Loading : LoginUiState
    data class Loaded(
        val username: String = "",
        val password: String = "",
        val isValid: Boolean = false,
        val isLoginIncorrect: Boolean = false,
        val loggingIn: Boolean = false,
    ) : LoginUiState
}

class LoginViewModel(
    val navController: NavHostController,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        if(Preferences.Authorization.token != null) {
            viewModelScope.launch(Dispatchers.Main) {
                navController.navigate(Navigation.Inner.MediaIndex)
            }
        }
        else _uiState.value = LoginUiState.Loaded()
    }

    fun setUsername(username: String) {
        val state = _uiState.value
        if(state !is LoginUiState.Loaded) return
        _uiState.value = state.copy(
            username = username,
            isValid = state.password.isNotEmpty() && username.isNotBlank(),
            isLoginIncorrect = false
        )
    }

    fun setPassword(password: String) {
        val state = _uiState.value
        if(state !is LoginUiState.Loaded) return
        _uiState.value = state.copy(
            password = password,
            isValid = password.isNotEmpty() && state.username.isNotBlank(),
            isLoginIncorrect = false
        )
    }

    fun changeInstance() {
        Preferences.instanceUrl = null
        navController.navigate(Navigation.InstanceSelection)
    }

    fun login() {
        val state = uiState.value
        if(state !is LoginUiState.Loaded) return
        if(!state.isValid) return

        _uiState.value = state.copy(loggingIn = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = Api.instance.Users().login(
                LoginRequest(
                    username = state.username,
                    password = state.password,
                )
            )

            when (result) {
                is Result.Error<*> -> {
                    _uiState.value = state.copy(isLoginIncorrect = true, loggingIn = false)
                    if(result.errors !is Errors.ApiErrors || result.errors.status != HttpStatusCode.Unauthorized) {
                        with(errorHandler) { result.handle() }
                    }
                }
                is Result.Success<LoginResult> -> {
                    Preferences.Authorization.assign(result.value)
                    viewModelScope.launch(Dispatchers.Main) {
                        navController.navigate(Navigation.Inner.MediaIndex)
                    }
                }
            }
        }
    }
}