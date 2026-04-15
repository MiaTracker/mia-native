package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import extensions.navigateFresh
import infrastructure.Platform
import infrastructure.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface InstanceSelectionUiState {
    object Loading : InstanceSelectionUiState
    data class Loaded(
        val url: String = "",
        val isValid: Boolean = false,
        val instanceExists: Boolean? = null,
        val connecting: Boolean = false,
    ) : InstanceSelectionUiState
}

class InstanceSelectionViewModel(
    private val _navController: NavHostController
) : ViewModel() {
    private val _uiState: MutableStateFlow<InstanceSelectionUiState> = MutableStateFlow(InstanceSelectionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val url = Preferences.instanceUrl
            if(url != null) {
                var result = Api.ping(url)
                if(result) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _navController.navigateFresh(Navigation.Login)
                    }
                    return@launch
                }
                else if(Platform.hasFixedInstance) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _navController.navigateFresh(Navigation.InstanceUnreachable)
                    }
                    return@launch
                }
            }
            _uiState.value = InstanceSelectionUiState.Loaded()
        }
    }


    fun setUrl(url: String) =
        when(val state = _uiState.value) {
            is InstanceSelectionUiState.Loading -> {}
            is InstanceSelectionUiState.Loaded -> _uiState.value = state.copy(url = url, isValid = url.isNotBlank(), instanceExists = null)
        }

    fun test() {
        val state = _uiState.value
        if(state !is InstanceSelectionUiState.Loaded) return

        _uiState.value = state.copy(connecting = true)
        viewModelScope.launch(Dispatchers.Unconfined) {
            val result = Api.ping(state.url)
            if(result) {
                _uiState.value = state.copy(connecting = false, instanceExists = true)
            } else {
                _uiState.value = state.copy(instanceExists = false, connecting = false)
            }
        }
    }

    fun connect() {
        val state = _uiState.value
        if(state !is InstanceSelectionUiState.Loaded) return

        _uiState.value = state.copy(connecting = true)
        viewModelScope.launch(Dispatchers.Unconfined) {
            val url = state.url
            val result = Api.ping(url)
            if(result) {
                Preferences.instanceUrl = url
                viewModelScope.launch(Dispatchers.Main) {
                    _navController.navigateFresh(Navigation.Login)
                }
            } else {
                _uiState.value = state.copy(instanceExists = false, connecting = false)
            }
        }
    }
}