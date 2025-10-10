package view_models

import Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.*
import enums.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed interface ImageSelectionUiState {
    sealed interface LoadedImageSelectionUiState : ImageSelectionUiState {
        val images: List<MediaImage>
    }

    object Loading: ImageSelectionUiState
    data class BackdropSelection(
        override val images: List<MediaImage>
    ) : LoadedImageSelectionUiState
    data class PosterSelection(
        override val images: List<MediaImage>
    ) : LoadedImageSelectionUiState
}

sealed interface MediaDetailsUiState<T: MediaDetails> {
    class Loading<T: MediaDetails> : MediaDetailsUiState<T>
    data class Loaded<T: MediaDetails>(
        val mediaDetails: T,

        val imageSelectionState: ImageSelectionUiState? = null
    ) : MediaDetailsUiState<T>
}

sealed interface MediaDetailsAdapter<T: MediaDetails> {
    suspend fun get(): Result<T>
    suspend fun createTitle(title: TitleCreate): Result<Unit>
    suspend fun setPrimaryTitle(titleId: Int): Result<Unit>
    suspend fun deleteTitle(titleId: Int): Result<Unit>
    suspend fun createGenre(genre: GenreCreate): Result<Unit>
    suspend fun deleteGenre(genreId: Int): Result<Unit>
    suspend fun createTag(tag: TagCreate): Result<Unit>
    suspend fun deleteTag(tagId: Int): Result<Unit>
    suspend fun addToWatchlist(): Result<Unit>
    suspend fun removeFromWatchlist(): Result<Unit>
    suspend fun createSource(source: SourceCreate): Result<Unit>
    suspend fun updateSource(source: Source): Result<Unit>
    suspend fun deleteSource(sourceId: Int): Result<Unit>
    suspend fun createLog(log: LogCreate): Result<Unit>
    suspend fun updateLog(log: Log): Result<Unit>
    suspend fun deleteLog(logId: Int): Result<Unit>
    suspend fun getBackdrops(): Result<List<MediaImage>>
    suspend fun setDefaultBackdrop(path: String): Result<Unit>
    suspend fun getPosters(): Result<List<MediaImage>>
    suspend fun setDefaultPoster(path: String): Result<Unit>

    class MovieDetailsAdapter(private val id: Int) : MediaDetailsAdapter<MovieDetails> {
        override suspend fun get() = Api.instance.Movies().Id(id).get()
        override suspend fun createTitle(title: TitleCreate) = Api.instance.Movies().Id(id).Titles().create(title)
        override suspend fun setPrimaryTitle(titleId: Int) = Api.instance.Movies().Id(id).Titles().Id(titleId).primary()
        override suspend fun deleteTitle(titleId: Int) = Api.instance.Movies().Id(id).Titles().Id(titleId).delete()
        override suspend fun createGenre(genre: GenreCreate) = Api.instance.Movies().Id(id).Genres().create(genre)
        override suspend fun deleteGenre(genreId: Int) = Api.instance.Movies().Id(id).Genres().Id(genreId).delete()
        override suspend fun createTag(tag: TagCreate) = Api.instance.Movies().Id(id).Tags().create(tag)
        override suspend fun deleteTag(tagId: Int) = Api.instance.Movies().Id(id).Tags().Id(tagId).delete()
        override suspend fun addToWatchlist() = Api.instance.Watchlist().add(id)
        override suspend fun removeFromWatchlist() = Api.instance.Watchlist().remove(id)
        override suspend fun createSource(source: SourceCreate) = Api.instance.Movies().Id(id).Sources().create(source)
        override suspend fun updateSource(source: Source) = Api.instance.Movies().Id(id).Sources().Id(source.id).update(source)
        override suspend fun deleteSource(sourceId: Int) = Api.instance.Movies().Id(id).Sources().Id(sourceId).delete()
        override suspend fun createLog(log: LogCreate) = Api.instance.Movies().Id(id).Logs().create(log)
        override suspend fun updateLog(log: Log) = Api.instance.Movies().Id(id).Logs().Id(log.id).update(log)
        override suspend fun deleteLog(logId: Int) = Api.instance.Movies().Id(id).Logs().Id(logId).delete()
        override suspend fun getBackdrops(): Result<List<MediaImage>> = Api.instance.Movies().Id(id).Backdrops().index()
        override suspend fun setDefaultBackdrop(path: String): Result<Unit> = Api.instance.Movies().Id(id).Backdrops().default(path)
        override suspend fun getPosters(): Result<List<MediaImage>> = Api.instance.Movies().Id(id).Posters().index()
        override suspend fun setDefaultPoster(path: String): Result<Unit> = Api.instance.Movies().Id(id).Posters().default(path)
    }

    class SeriesDetailsAdapter(private val id: Int) : MediaDetailsAdapter<SeriesDetails> {
        override suspend fun get() = Api.instance.Series().Id(id).get()
        override suspend fun createTitle(title: TitleCreate) = Api.instance.Series().Id(id).Titles().create(title)
        override suspend fun setPrimaryTitle(titleId: Int) = Api.instance.Series().Id(id).Titles().Id(titleId).primary()
        override suspend fun deleteTitle(titleId: Int) = Api.instance.Series().Id(id).Titles().Id(titleId).delete()
        override suspend fun createGenre(genre: GenreCreate) = Api.instance.Series().Id(id).Genres().create(genre)
        override suspend fun deleteGenre(genreId: Int) = Api.instance.Series().Id(id).Genres().Id(genreId).delete()
        override suspend fun createTag(tag: TagCreate) = Api.instance.Series().Id(id).Tags().create(tag)
        override suspend fun deleteTag(tagId: Int) = Api.instance.Series().Id(id).Tags().Id(tagId).delete()
        override suspend fun addToWatchlist() = Api.instance.Watchlist().add(id)
        override suspend fun removeFromWatchlist() = Api.instance.Watchlist().remove(id)
        override suspend fun createSource(source: SourceCreate) = Api.instance.Series().Id(id).Sources().create(source)
        override suspend fun updateSource(source: Source) = Api.instance.Series().Id(id).Sources().Id(source.id).update(source)
        override suspend fun deleteSource(sourceId: Int) = Api.instance.Series().Id(id).Sources().Id(sourceId).delete()
        override suspend fun createLog(log: LogCreate) = Api.instance.Series().Id(id).Logs().create(log)
        override suspend fun updateLog(log: Log) = Api.instance.Series().Id(id).Logs().Id(log.id).update(log)
        override suspend fun deleteLog(logId: Int) = Api.instance.Series().Id(id).Logs().Id(logId).delete()
        override suspend fun getBackdrops(): Result<List<MediaImage>> = Api.instance.Series().Id(id).Backdrops().index()
        override suspend fun setDefaultBackdrop(path: String): Result<Unit> = Api.instance.Series().Id(id).Backdrops().default(path)
        override suspend fun getPosters(): Result<List<MediaImage>> = Api.instance.Series().Id(id).Posters().index()
        override suspend fun setDefaultPoster(path: String): Result<Unit> = Api.instance.Series().Id(id).Posters().default(path)
    }
}

class MediaDetailsViewModel<T: MediaDetails>(
    private val adapter: MediaDetailsAdapter<T>
) : ViewModel() {
    private val _uiState: MutableStateFlow<MediaDetailsUiState<T>> = MutableStateFlow(MediaDetailsUiState.Loading())
    val uiState: StateFlow<MediaDetailsUiState<T>> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            refresh()
        }
    }

    private suspend fun refresh() {
        val result = adapter.get()

        when(result) {
            is Result.Error<*> -> TODO()
            is Result.Success<T> -> {
                _uiState.value = MediaDetailsUiState.Loaded(result.value)
            }
        }
    }

    abstract inner class ImageSelection {
        fun openImageSelection() {
            val state = _uiState.value
            if(state !is MediaDetailsUiState.Loaded) return
            _uiState.value = state.copy(imageSelectionState = ImageSelectionUiState.Loading)

            viewModelScope.launch(Dispatchers.IO) {
                val result = getImagesApi()

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<List<MediaImage>> -> {
                        val state = _uiState.value
                        if(state !is MediaDetailsUiState.Loaded) return@launch
                        _uiState.value = state.copy(imageSelectionState = createState(result.value))
                    }
                }
            }
        }

        fun closeImageSelection() {
            val state = _uiState.value
            if(state !is MediaDetailsUiState.Loaded) return
            _uiState.value = state.copy(imageSelectionState = null)
        }

        fun setImage(backdrop: MediaImage) {
            if(!backdrop.current) {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = setDefaultImageApi(backdrop.filePath)

                    when(result) {
                        is Result.Error<*> -> TODO()
                        is Result.Success<*> -> {
                            refresh()
                            closeImageSelection()
                        }
                    }
                }
            }
            else {
                closeImageSelection()
            }
        }

        protected abstract suspend fun getImagesApi(): Result<List<MediaImage>>
        protected abstract suspend fun setDefaultImageApi(path: String): Result<Unit>
        protected abstract fun createState(images: List<MediaImage>): ImageSelectionUiState.LoadedImageSelectionUiState
    }

    inner class Backdrops : ImageSelection() {
        override suspend fun getImagesApi(): Result<List<MediaImage>> = adapter.getBackdrops()
        override suspend fun setDefaultImageApi(path: String): Result<Unit> = adapter.setDefaultBackdrop(path)
        override fun createState(images: List<MediaImage>): ImageSelectionUiState.LoadedImageSelectionUiState =
            ImageSelectionUiState.BackdropSelection(images)
    }

    inner class Posters : ImageSelection() {
        override suspend fun getImagesApi(): Result<List<MediaImage>> = adapter.getPosters()
        override suspend fun setDefaultImageApi(path: String): Result<Unit> = adapter.setDefaultPoster(path)
        override fun createState(images: List<MediaImage>): ImageSelectionUiState.LoadedImageSelectionUiState =
            ImageSelectionUiState.PosterSelection(images)
    }

    inner class AlternativeTitles {

        fun create(name: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.createTitle(
                    TitleCreate(name = name)
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<Unit> -> {
                        refresh()
                    }
                }
            }
        }

        fun setPrimary(title: AlternativeTitle) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.setPrimaryTitle(title.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<Unit> -> {
                        refresh()
                    }
                }
            }
        }

        fun delete(title: AlternativeTitle) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.deleteTitle(title.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }

    inner class Genres {
        fun create(name: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.createGenre(
                    GenreCreate(name = name)
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<Unit> -> {
                        refresh()
                    }
                }
            }
        }

        fun delete(genre: Genre) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.deleteGenre(genre.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }

    inner class Tags {
        fun create(name: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.createTag(
                    TagCreate(name = name)
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<Unit> -> {
                        refresh()
                    }
                }
            }
        }

        fun delete(tag: Tag) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.deleteTag(tag.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }

    inner class Watchlist {
        fun add() {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.addToWatchlist()

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }

        fun remove() {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.removeFromWatchlist()

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }

    inner class Sources {
        fun create(name: String, type: SourceType, url: String) {
            if(name.isBlank()) return
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.createSource(
                    source = SourceCreate(
                        name = name,
                        type = type,
                        url = url,
                    )
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }

        fun update(source: Source) {
            if(source.name.isBlank()) return
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.updateSource(
                    source = source
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }

        }

        fun delete(source: Source) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.deleteSource(source.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }

    inner class Logs {

        fun create(
            date: LocalDate,
            source: String,
            stars: Float?,
            completed: Boolean,
            comment: String?
        ) {
            if(source.isBlank()) return
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.createLog(
                    log = LogCreate(
                        date = date,
                        source = source,
                        stars = stars,
                        completed = completed,
                        comment = comment
                    )
                )

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }

        fun update(log: Log) {
            if(log.source.isBlank()) return
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.updateLog(log = log)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }

        fun delete(log: Log) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = adapter.deleteLog(log.id)

                when(result) {
                    is Result.Error<*> -> TODO()
                    is Result.Success<*> -> {
                        refresh()
                    }
                }
            }
        }
    }
}