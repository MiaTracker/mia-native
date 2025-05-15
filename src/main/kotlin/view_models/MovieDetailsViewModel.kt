package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.AlternativeTitle
import data_objects.Genre
import data_objects.GenreCreate
import data_objects.MovieDetails
import data_objects.Result
import data_objects.Source
import data_objects.SourceCreate
import data_objects.Tag
import data_objects.TagCreate
import data_objects.TitleCreate
import enums.SourceType
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            refresh()
        }
    }

    private suspend fun refresh() {
        val result = Api.instance.Movies().Id(id).get()

        when(result) {
            is Result.Error<*> -> TODO()
            is Result.Success<MovieDetails> -> {
                _uiState.value = MovieDetailsUiState.Loaded(result.value)
            }
        }
    }

    inner class AlternativeTitles {

        fun create(name: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = Api.instance.Movies().Id(id).Titles().create(
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
                val result = Api.instance.Movies().Id(id).Titles().Id(title.id).primary()

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
                val result = Api.instance.Movies().Id(id).Titles().Id(title.id).delete()

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
                val result = Api.instance.Movies().Id(id).Genres().create(
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
                val result = Api.instance.Movies().Id(id).Genres().Id(genre.id).delete()

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
                val result = Api.instance.Movies().Id(id).Tags().create(
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
                val result = Api.instance.Movies().Id(id).Tags().Id(tag.id).delete()

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
                val result = Api.instance.Movies().Id(id).Sources().create(
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
                val result = Api.instance.Movies().Id(id).Sources().Id(source.id).update(
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
                val result = Api.instance.Movies().Id(id).Sources().Id(source.id).delete()

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

    }
}