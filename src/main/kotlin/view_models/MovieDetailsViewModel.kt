package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.MovieDetails
import data_objects.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MovieDetailsUiState {
    object Loading : MovieDetailsUiState
    data class Loaded(
        val movieDetails: MovieDetails,
    ) : MovieDetailsUiState
}

class MovieDetailsViewModel(
    private val id: Int
) : ViewModel() {
    private val _uiState: MutableStateFlow<MovieDetailsUiState> = MutableStateFlow(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val result = Api.Movies.Id(id).get()

            when(result) {
                is Result.Error<*> -> TODO()
                is Result.Success<MovieDetails> -> {
                    _uiState.value = MovieDetailsUiState.Loaded(result.value)
                }
            }
        }
    }
}