import data_objects.*
import data_objects.MovieDetails
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
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

                    suspend fun primary(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.post(BASE_URL) {
                                header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                                url {
                                    appendPathSegments("movies", movieId.toString(), "titles", titleId.toString(), "primary")
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

            inner class Genres {
                suspend fun create(genre: GenreCreate): Result<Unit> {
                    val requestBody = _json.encodeToString(genre)

                    val response = httpClient().use { client ->
                        client.post(BASE_URL) {
                            header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                            header(HttpHeaders.ContentType, "application/json")
                            url {
                                appendPathSegments("movies", movieId.toString(), "genres")
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
                    val genreId: Int
                ) {
                    suspend fun delete(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.delete(BASE_URL) {
                                header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                                url {
                                    appendPathSegments("movies", movieId.toString(), "genres", genreId.toString())
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

            inner class Tags {
                suspend fun create(tag: TagCreate): Result<Unit> {
                    val requestBody = _json.encodeToString(tag)

                    val response = httpClient().use { client ->
                        client.post(BASE_URL) {
                            header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                            header(HttpHeaders.ContentType, "application/json")
                            url {
                                appendPathSegments("movies", movieId.toString(), "tags")
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
                    val tagId: Int
                ) {
                    suspend fun delete(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.delete(BASE_URL) {
                                header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                                url {
                                    appendPathSegments("movies", movieId.toString(), "tags", tagId.toString())
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

            inner class Sources {
                inner class Id(
                    val sourceId: Int
                ) {
                    suspend fun delete(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.delete(BASE_URL) {
                                header(HttpHeaders.Authorization, "Bearer $AUTHORIZATION_TOKEN")
                                url {
                                    appendPathSegments("movies", movieId.toString(), "sources", sourceId.toString())
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