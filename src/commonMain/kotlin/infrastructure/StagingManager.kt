package infrastructure

object StagingManager {
    var ids: Set<Int> = emptySet()
    private set

    fun toggle(id: Int) {
        ids = if (id in ids) ids - id else ids + id
    }
}
