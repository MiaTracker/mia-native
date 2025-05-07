package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data_objects.AlternativeTitle
import data_objects.TitleCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import data_objects.Result


data class AlternativeTitlesRowUiState(
    val titles: List<AlternativeTitle>,
    val awaitingServerResponse: Boolean
)

class AlternativeTitlesRowViewModel(
    alternativeTitles: List<AlternativeTitle>,
    val baseApiCall: Api.Movies.Id,
    val refreshParent: suspend () -> Unit
) : ViewModel() {

    private val _uiState: MutableStateFlow<AlternativeTitlesRowUiState> = MutableStateFlow(AlternativeTitlesRowUiState(
        titles = alternativeTitles,
        awaitingServerResponse = false)
    )
    val uiState = _uiState.asStateFlow()

    fun create(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(awaitingServerResponse = true)

            val result = baseApiCall.Titles().create(
                TitleCreate(name = name)
            )

            when(result) {
                is Result.Error<*> -> TODO()
                is Result.Success<*> -> {
                    refreshParent()
                }
            }


        }
    }
}