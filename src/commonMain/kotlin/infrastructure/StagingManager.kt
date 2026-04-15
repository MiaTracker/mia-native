package infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object StagingManager {
    private val _ids = MutableStateFlow<Set<Int>>(emptySet())
    val ids = _ids.asStateFlow()

    fun toggle(id: Int) {
        _ids.value = if (id in _ids.value) _ids.value - id else _ids.value + id
    }
}
