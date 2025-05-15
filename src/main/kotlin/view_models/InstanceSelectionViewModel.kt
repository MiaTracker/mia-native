package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InstanceSelectionUiState(
    val url: String = "",
    val isValid: Boolean = false,
    val instanceExists: Boolean? = null,
    val connecting: Boolean = false,
)

class InstanceSelectionViewModel(
    private val _navController: NavHostController
) : ViewModel() {
    private val _uiState = MutableStateFlow(InstanceSelectionUiState())
    val uiState = _uiState.asStateFlow()

    fun setUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url, isValid = url.isNotBlank(), instanceExists = null)
    }

    fun test() {
        _uiState.value = _uiState.value.copy(connecting = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = Api.Instance(_uiState.value.url).ping()
            if(result) {
                _uiState.value = _uiState.value.copy(connecting = false, instanceExists = true)
            } else {
                _uiState.value = _uiState.value.copy(instanceExists = false, connecting = false)
            }
        }
    }

    fun connect() {
        _uiState.value = _uiState.value.copy(connecting = true)
        viewModelScope.launch(Dispatchers.IO) {
            val url = _uiState.value.url
            val result = Api.Instance(url).ping()
            if(result) {
                Api.connectDefault(url)
                viewModelScope.launch(Dispatchers.Main) {
                    _navController.navigate(Navigation.Login)
                }
            } else {
                _uiState.value = _uiState.value.copy(instanceExists = false, connecting = false)
            }
        }
    }
}