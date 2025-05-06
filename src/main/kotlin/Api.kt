import data_objects.ApiErrorList
import data_objects.MediaIndex
import data_objects.MovieDetails
import data_objects.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object Api {
    private const val BASE_URL = "http://localhost:3000"

    @OptIn(ExperimentalSerializationApi::class)
    private val _json = Json {
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private fun httpClient(): HttpClient = HttpClient(OkHttp)


    object Movies {
        suspend fun index(offset: Int? = null, limit: Int? = null): Result<List<MediaIndex>> {
            val response = httpClient().use { client ->
                client.get(BASE_URL) {
                    header(HttpHeaders.Authorization, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1ZjkwOWZiOS0zOGU3LTRhMmQtYWM4YS1lMjY1ZTYwYmE5YTkiLCJpYXQiOjE3NDU4NjM4MDUsImV4cCI6MTc1MTA0NzgwNSwidHlwZSI6IlVzZXJUb2tlbiJ9.BjxDzGoMuq0VdUpVKF-Htc086xgbpl67tQpe_BW7uOY")
                    url {
                        appendPathSegments("movies")
                        if (offset != null) {
                            parameters.append("offset", offset.toString())
                        }
                        if (limit != null) {
                            parameters.append("limit", limit.toString())
                        }
                    }
                }
            }

            val body = response.bodyAsText()

            return if(response.status.isSuccess()) {
                Result.Success(_json.decodeFromString<List<MediaIndex>>(body))
            } else {
                Result.Error(_json.decodeFromString<ApiErrorList>(body))
            }
        }

        class Id(
            private val id: Int
        ) {
            suspend fun get(): Result<MovieDetails> {
                val response = httpClient().use { client ->
                    client.get(BASE_URL) {
                        header(HttpHeaders.Authorization, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1ZjkwOWZiOS0zOGU3LTRhMmQtYWM4YS1lMjY1ZTYwYmE5YTkiLCJpYXQiOjE3NDU4NjM4MDUsImV4cCI6MTc1MTA0NzgwNSwidHlwZSI6IlVzZXJUb2tlbiJ9.BjxDzGoMuq0VdUpVKF-Htc086xgbpl67tQpe_BW7uOY")
                        url {
                            appendPathSegments("movies", id.toString())
                        }
                    }
                }

                val body = response.bodyAsText()

                return if(response.status.isSuccess()) {
                    Result.Success(_json.decodeFromString<MovieDetails>(body))
                } else {
                    Result.Error(_json.decodeFromString<ApiErrorList>(body))
                }
            }
        }
    }
}