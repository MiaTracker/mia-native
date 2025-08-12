package view_models

import Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import data_objects.ExternalMediaIndex
import data_objects.InternalMediaIndex
import data_objects.Result
import data_objects.SearchResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

sealed interface MainUiState {
    val searchQuery: String
    val searchQueryCommited: Boolean

    data class Loading(
        override val searchQuery: String = "",
        override val searchQueryCommited: Boolean = false
    ) : MainUiState
    data class Loaded(
        val internal: List<InternalMediaIndex>,
        val external: List<ExternalMediaIndex> = emptyList(),

        override val searchQuery: String = "",
        override val searchQueryCommited: Boolean = false,
        val searchQueryValid: Boolean = true
    ) : MainUiState
}

class MoviesIndexViewModel(private val navController: NavController) : ViewModel() {
    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Loading())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        load()
    }

    fun searchQueryChanged(query: String) {
        _uiState.value = when (val state = _uiState.value) {
            is MainUiState.Loading -> {
                state.copy(searchQuery = query, searchQueryCommited = false)
            }
            is MainUiState.Loaded -> {
                state.copy(searchQuery = query, searchQueryCommited = false, searchQueryValid = true)
            }
        }
        load()
    }

    fun searchQueryCommited() {
        _uiState.value = when (val state = _uiState.value) {
            is MainUiState.Loaded -> {
                state.copy(searchQueryCommited = true)
            }
            is MainUiState.Loading -> {
                state.copy(searchQueryCommited = true)
            }
        }
        load()
    }

    fun addExternal(externalId: Int) {
        viewModelScope.launch {
            val result = Api.instance.Movies().create(externalId = externalId)
            when (result) {
                is Result.Error<*> -> TODO()
                is Result.Success<Int> -> {
                    navController.navigate(Navigation.Inner.MovieDetails(result.value))
                    load()
                }
            }
        }
    }

    private fun load() {
        val state = _uiState.value
        val oldJob = fetchJob
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            oldJob?.cancelAndJoin()

            if(state.searchQuery.isEmpty()) {
                val result = Api.instance.Movies().index()
                when (result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<List<InternalMediaIndex>> -> {
                        _uiState.value = MainUiState.Loaded(
                            internal = result.value,

                            searchQuery = uiState.value.searchQuery,
                            searchQueryCommited = uiState.value.searchQueryCommited,
                            searchQueryValid = true
                        )
                    }
                }
            } else {
                val result = Api.instance.Movies().search(state.searchQuery, state.searchQueryCommited)
                when (result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<SearchResults> -> {
                        _uiState.value = MainUiState.Loaded(
                            internal = result.value.indexes,
                            external = result.value.external,

                            searchQuery = uiState.value.searchQuery,
                            searchQueryCommited = uiState.value.searchQueryCommited,
                            searchQueryValid = result.value.queryValid
                        )
                    }
                }
            }
        }
    }
}