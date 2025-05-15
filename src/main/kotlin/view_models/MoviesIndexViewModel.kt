package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.MediaIndex
import data_objects.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

sealed interface MainUiState {
    object Loading : MainUiState
    data class Loaded(
        val media: List<MediaIndex>
    ) : MainUiState
}

class MoviesIndexViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Api.instance.Movies().index()
            when (result) {
                is Result.Error<*> -> TODO()
                is Result.Success<List<MediaIndex>> -> {
                    _uiState.value = MainUiState.Loaded(result.value)
                }
            }
        }
    }
}