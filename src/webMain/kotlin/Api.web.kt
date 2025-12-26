import io.ktor.client.*
import io.ktor.client.engine.js.*

actual fun platformHttpClient(): HttpClient = HttpClient(Js)