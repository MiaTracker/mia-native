package view_models

import Api
import Navigation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import data_objects.LoginRequest
import data_objects.LoginResult
import data_objects.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    val navController: NavHostController
) : ViewModel() {

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Api.Users.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            viewModelScope.launch(Dispatchers.Main) {
                when (result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<LoginResult> -> {
                        Api.loginResult = result.value
                        navController.navigate(Navigation.Inner.MoviesIndex)
                    }
                }
            }
        }
    }
}