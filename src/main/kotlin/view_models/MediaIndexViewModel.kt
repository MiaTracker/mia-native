package view_models

import Api
import Navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import data_objects.ExternalMediaIndex
import data_objects.InternalMediaIndex
import data_objects.Result
import data_objects.SearchResults
import enums.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

sealed interface MediaIndexUiState {
    val searchQuery: String
    val searchQueryCommited: Boolean

    data class Loading(
        override val searchQuery: String = "",
        override val searchQueryCommited: Boolean = false
    ) : MediaIndexUiState
    data class Loaded(
        val internal: List<InternalMediaIndex>,
        val external: List<ExternalMediaIndex> = emptyList(),

        override val searchQuery: String = "",
        override val searchQueryCommited: Boolean = false,
        val searchQueryValid: Boolean = true
    ) : MediaIndexUiState
}

sealed interface IndexAdapter {
    suspend fun index(): Result<List<InternalMediaIndex>>
    suspend fun search(query: String, commited: Boolean): Result<SearchResults>
    suspend fun create(externalId: Int, type: MediaType): Result<Int>
    fun navigateTo(id: Int, type: MediaType)

    class MediaIndexAdapter(private val navController: NavController) : IndexAdapter {
        override suspend fun index(): Result<List<InternalMediaIndex>> = Api.instance.Media().index()
        override suspend fun search(query: String,commited: Boolean) = Api.instance.Media().search(query, commited)
        override suspend fun create(externalId: Int, type: MediaType): Result<Int> =
            when(type) {
                MediaType.Movie -> Api.instance.Movies().create(externalId = externalId)
                MediaType.Series -> Api.instance.Series().create(externalId = externalId)
            }
        override fun navigateTo(id: Int, type: MediaType) =
            when (type) {
                MediaType.Movie -> navController.navigate(Navigation.Inner.MovieDetails(id))
                MediaType.Series -> navController.navigate(Navigation.Inner.SeriesDetails(id))
            }
    }

    class MoviesIndexAdapter(private val navController: NavController) : IndexAdapter {
        override suspend fun index() = Api.instance.Movies().index()
        override suspend fun search(query: String, commited: Boolean) = Api.instance.Movies().search(query, commited)
        override suspend fun create(externalId: Int, type: MediaType) = Api.instance.Movies().create(externalId = externalId)
        override fun navigateTo(id: Int, type: MediaType) = navController.navigate(Navigation.Inner.MovieDetails(id))
    }

    class SeriesIndexAdapter(private val navController: NavController) : IndexAdapter {
        override suspend fun index() = Api.instance.Series().index()
        override suspend fun search(query: String, commited: Boolean) = Api.instance.Series().search(query, commited)
        override suspend fun create(externalId: Int, type: MediaType) = Api.instance.Series().create(externalId = externalId)
        override fun navigateTo(id: Int, type: MediaType) = navController.navigate(Navigation.Inner.SeriesDetails(id))
    }

    class WatchlistIndexAdapter(private val navController: NavController) : IndexAdapter {
        override suspend fun index(): Result<List<InternalMediaIndex>> = Api.instance.Watchlist().index()
        override suspend fun search(query: String, commited: Boolean): Result<SearchResults> = Api.instance.Watchlist().search(query, commited)
        override suspend fun create(externalId: Int, type: MediaType): Result<Int> = throw Exception()
        override fun navigateTo(id: Int, type: MediaType) =
            when (type) {
                MediaType.Movie -> navController.navigate(Navigation.Inner.MovieDetails(id))
                MediaType.Series -> navController.navigate(Navigation.Inner.SeriesDetails(id))
            }
    }
}

class MediaIndexViewModel(private val adapter: IndexAdapter) : ViewModel() {
    private val _uiState: MutableStateFlow<MediaIndexUiState> = MutableStateFlow(MediaIndexUiState.Loading())
    val uiState: StateFlow<MediaIndexUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        load()
    }

    fun searchQueryChanged(query: String) {
        _uiState.value = when (val state = _uiState.value) {
            is MediaIndexUiState.Loading -> {
                state.copy(searchQuery = query, searchQueryCommited = false)
            }
            is MediaIndexUiState.Loaded -> {
                state.copy(searchQuery = query, searchQueryCommited = false, searchQueryValid = true)
            }
        }
        load()
    }

    fun searchQueryCommited() {
        _uiState.value = when (val state = _uiState.value) {
            is MediaIndexUiState.Loaded -> {
                state.copy(searchQueryCommited = true)
            }
            is MediaIndexUiState.Loading -> {
                state.copy(searchQueryCommited = true)
            }
        }
        load()
    }

    fun addExternal(index: ExternalMediaIndex) {
        viewModelScope.launch {
            val result = adapter.create(externalId = index.externalId, type = index.type)
            when (result) {
                is Result.Error<*> -> TODO()
                is Result.Success<Int> -> {
                    adapter.navigateTo(id = result.value, type = index.type)
                    load()
                }
            }
        }
    }

    fun openDetails(media: InternalMediaIndex) {
        adapter.navigateTo(
            id = media.id,
            type = media.type
        )
    }

    private fun load() {
        val state = _uiState.value
        val oldJob = fetchJob
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            oldJob?.cancelAndJoin()

            if(state.searchQuery.isEmpty()) {
                val result = adapter.index()
                when (result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<List<InternalMediaIndex>> -> {
                        _uiState.value = MediaIndexUiState.Loaded(
                            internal = result.value,

                            searchQuery = uiState.value.searchQuery,
                            searchQueryCommited = uiState.value.searchQueryCommited,
                            searchQueryValid = true
                        )
                    }
                }
            } else {
                val result = adapter.search(
                    query = state.searchQuery,
                    commited = state.searchQueryCommited
                )
                when (result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<SearchResults> -> {
                        _uiState.value = MediaIndexUiState.Loaded(
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