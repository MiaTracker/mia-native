package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import data_objects.InternalMediaIndex
import data_objects.Result
import data_objects.Stats
import enums.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StatisticsUiState {
    object Loading : StatisticsUiState
    data class Loaded(val stats: Stats) : StatisticsUiState
}

class StatisticsViewModel(
    val navController: NavHostController
) : ViewModel() {
    private val _uiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = Api.instance.statistics()) {
                is Result.Error<*> -> TODO()
                is Result.Success<Stats> -> {
                    _uiState.value = StatisticsUiState.Loaded(
                        stats = result.value
                    )
                }
            }
        }
    }

    fun openMediaDetails(media: InternalMediaIndex) {
        navController.navigate(
            route = when (media.type) {
                MediaType.Movie -> Navigation.Inner.MovieDetails(media.id)
                MediaType.Series -> Navigation.Inner.SeriesDetails(media.id)
            }
        )
    }
}