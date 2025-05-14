import data_objects.*
import data_objects.MovieDetails
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object Api {
    private const val BASE_URL = "http://localhost:3000"

    var loginResult: LoginResult? = null


    @OptIn(ExperimentalSerializationApi::class)
    private val _json = Json {
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private fun httpClient(): HttpClient = HttpClient(OkHttp)
        .config {
            defaultRequest {
                loginResult?.let { result ->
                    header(HttpHeaders.ContentType, "application/json")
                    header(HttpHeaders.Authorization, "Bearer ${result.token}")
                }
            }
        }


    object Movies {
        suspend fun index(offset: Int? = null, limit: Int? = null): Result<List<MediaIndex>> {
            val response = httpClient().use { client ->
                client.get(BASE_URL) {
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
                suspend fun create(source: SourceCreate): Result<Unit> {
                    val requestBody = _json.encodeToString(source)

                    val response = httpClient().use { client ->
                        client.post(BASE_URL) {
                            url {
                                appendPathSegments("movies", movieId.toString(), "sources")
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
                    val sourceId: Int
                ) {
                    suspend fun update(source: Source): Result<Unit> {
                        val requestBody = _json.encodeToString(source)

                        val response = httpClient().use { client ->
                            client.patch(BASE_URL) {
                                url {
                                    appendPathSegments("movies", movieId.toString(), "sources", sourceId.toString())
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

                    suspend fun delete(): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.delete(BASE_URL) {
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

    object Users {
        suspend fun login(request: LoginRequest): Result<LoginResult> {
            val requestBody = _json.encodeToString(request)

            val response = httpClient().use { client ->
                client.post(BASE_URL) {
                    url {
                        appendPathSegments("users", "login")
                    }
                    setBody(requestBody)
                }
            }


            val body = response.bodyAsText()
            return if(response.status.isSuccess()) {
                Result.Success(_json.decodeFromString<LoginResult>(body))
            } else {
                Result.Error(_json.decodeFromString<ApiErrorList>(body))
            }
        }
    }
}