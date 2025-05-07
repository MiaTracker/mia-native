import data_objects.ApiErrorList
import data_objects.MediaIndex
import data_objects.MovieDetails
import data_objects.Result
import data_objects.TitleCreate
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object Api {
    private const val BASE_URL = "http://localhost:3000"
    private const val AUTHORIZATION_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkM2MxNWM4Yi00NDdmLTRlYWMtYTY4ZC1iNDQ1NDQ3ZmRjZGUiLCJpYXQiOjE3NDY2MDgwODgsImV4cCI6MTc1MTc5MjA4OCwidHlwZSI6IlVzZXJUb2tlbiJ9.NXf9Mx6s9DxxqOPWe5FchlziMAG9a4bHj3mJscZc92c"

    @OptIn(ExperimentalSerializationApi::class)
    private val _json = Json {
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private fun httpClient(): HttpClient = HttpClient(OkHttp)


    object Movies {
        suspend fun index(offset: Int? = null, limit: Int? = null): Result<List<MediaIndex>> {
            val response = httpClient().use { client ->
                client.get(BASE_URL) {
                    header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
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
            private val movieId: Int
        ) {
            suspend fun get(): Result<MovieDetails> {
                val response = httpClient().use { client ->
                    client.get(BASE_URL) {
                        header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                        url {
                            appendPathSegments("movies", movieId.toString())
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

            inner class Titles {

                suspend fun create(title: TitleCreate): Result<Unit> {
                    val requestBody = _json.encodeToString(title)
                    val response = httpClient().use { client ->
                        client.post(BASE_URL) {
                            header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                            header(HttpHeaders.ContentType, "application/json")
                            url {
                                appendPathSegments("movies", movieId.toString(), "titles")
                            }
                            setBody(requestBody)
                        }
                    }

                    return if(response.status.isSuccess()) {
                        Result.Success(Unit)
                    } else {
                        val body = response.bodyAsText()
                        Result.Error(_json.decodeFromString<ApiErrorList>(body))
                    }
                }

                inner class Id(
                    private val titleId: Int
                ) {

                    suspend fun delete(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.delete(BASE_URL) {
                                header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                                url {
                                    appendPathSegments("movies", movieId.toString(), "titles", titleId.toString())
                                }
                            }
                        }


                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            val body = response.bodyAsText()
                            Result.Error(_json.decodeFromString<ApiErrorList>(body))
                        }

                    }
                }
            }
        }
    }
}