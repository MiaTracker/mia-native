package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import data_objects.LoginRequest
import data_objects.LoginResult
import data_objects.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isValid: Boolean = false,
    val isLoginIncorrect: Boolean = false,
    val loggingIn: Boolean = false,
)

class LoginViewModel(
    val navController: NavHostController
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun setUsername(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            isValid = _uiState.value.password.isNotEmpty() && username.isNotBlank(),
            isLoginIncorrect = false
        )
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isValid = password.isNotEmpty() && _uiState.value.username.isNotBlank(),
            isLoginIncorrect = false
        )
    }

    fun login() {
        val state = uiState.value
        if(!state.isValid) return

        _uiState.value = _uiState.value.copy(loggingIn = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = Api.instance.Users().login(
                LoginRequest(
                    username = state.username,
                    password = state.password,
                )
            )

            when (result) {
                is Result.Error<*> -> {
                    //TODO
                    _uiState.value = _uiState.value.copy(isLoginIncorrect = true, loggingIn = false)
                }
                is Result.Success<LoginResult> -> {
                    Api.instance.loginResult = result.value
                    viewModelScope.launch(Dispatchers.Main) {
                        navController.navigate(Navigation.Inner.MoviesIndex)
                    }
                }
            }
        }
    }
}