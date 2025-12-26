package infrastructure

actual object Platform {
    actual val hasFixedInstance: Boolean
        get() = false
}