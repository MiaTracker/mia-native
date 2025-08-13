import data_objects.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object Api {
    private var _instance: Instance? = null
    val instance: Instance
        get() = _instance ?: error("Instance not initialized")

    fun connectDefault(url: String) {
        _instance = Instance(url)
    }

    class Instance(private val baseUrl: String) {
        var loginResult: LoginResult? = null

        @OptIn(ExperimentalSerializationApi::class)
        private fun httpClient(): HttpClient = HttpClient(OkHttp)
            .config {
                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                        }
                    )
                }

                defaultRequest {
                    contentType(ContentType.Application.Json)

                    loginResult?.let { result ->
                        header(HttpHeaders.Authorization, "Bearer ${result.token}")
                    }
                }
            }

        suspend fun ping(): Boolean {
            try {
                val response = httpClient().use { client ->
                    client.get(baseUrl) {
                        url {
                            appendPathSegments("ping")
                        }
                    }
                }
                return response.status.isSuccess()
            } catch(_: Exception) { return false }
        }

        inner class Users {
            suspend fun login(request: LoginRequest): Result<LoginResult> {
                val response = httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments("users", "login")
                        }
                        setBody(request)
                    }
                }

                val body = response.bodyAsText()
                println(body)
                return if(response.status.isSuccess()) {
                    Result.Success(response.body<LoginResult>())
                } else {
                    Result.Error(response.body<ApiErrorList>())
                }
            }
        }

        inner class Media : BaseMediaApi() {
            override val subpath: String
                get() = "media"
        }

        inner class Movies : BaseMediaInstanceApi<MovieDetails>() {
            override val subpath: String
                get() = "movies"

            override val type: TypeInfo
                get() = TypeInfo(MovieDetails::class)
        }

        inner class Series : BaseMediaInstanceApi<SeriesDetails>() {
            override val subpath: String
                get() = "series"

            override val type: TypeInfo
                get() = TypeInfo(SeriesDetails::class)
        }

        abstract inner class BaseMediaApi {
            protected abstract val subpath: String

            suspend fun index(offset: Int? = null, limit: Int? = null): Result<List<InternalMediaIndex>> {
                val response = httpClient().use { client ->
                    client.get(baseUrl) {
                        url {
                            appendPathSegments(subpath)
                            if (offset != null) {
                                parameters.append("offset", offset.toString())
                            }
                            if (limit != null) {
                                parameters.append("limit", limit.toString())
                            }
                        }
                    }
                }

                return if (response.status.isSuccess()) {
                    Result.Success(response.body<List<InternalMediaIndex>>())
                } else {
                    Result.Error(response.body<ApiErrorList>())
                }
            }

            suspend fun search(
                query: String,
                committed: Boolean,
                offset: Int? = null,
                limit: Int? = null
            ): Result<SearchResults> {
                val response = httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments(subpath)
                            appendPathSegments("search")
                            parameters.append("committed", committed.toString())
                            if (offset != null) {
                                parameters.append("offset", offset.toString())
                            }
                            if (limit != null) {
                                parameters.append("limit", limit.toString())
                            }
                        }

                        setBody(
                            SearchRequest(
                                query = query,
                            )
                        )
                    }
                }

                return if (response.status.isSuccess()) {
                    Result.Success(response.body<SearchResults>())
                } else {
                    Result.Error(response.body<ApiErrorList>())
                }
            }
        }

        abstract inner class BaseMediaInstanceApi<T: MediaDetails> : BaseMediaApi() {
            protected abstract val type: TypeInfo

            suspend fun create(externalId: Int): Result<Int> {
                val response = httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments(subpath)
                            parameters.append("tmdb_id", externalId.toString())
                        }
                    }
                }

                return if (response.status.isSuccess()) {
                    Result.Success(response.body<Int>())
                } else {
                    Result.Error(response.body<ApiErrorList>())
                }
            }

            inner class Id(
                private val mediaId: Int
            ) {
                suspend fun get(): Result<T> {
                    val response = httpClient().use { client ->
                        client.get(baseUrl) {
                            url {
                                appendPathSegments(subpath, mediaId.toString())
                            }
                        }
                    }

                    return if(response.status.isSuccess()) {
                        Result.Success(response.body(type))
                    } else {
                        Result.Error(response.body<ApiErrorList>())
                    }
                }

                inner class Titles {

                    suspend fun create(title: TitleCreate): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "titles")
                                }
                                setBody(title)
                            }
                        }

                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            Result.Error(response.body<ApiErrorList>())
                        }
                    }

                    inner class Id(
                        private val titleId: Int
                    ) {

                        suspend fun primary(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.post(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "titles", titleId.toString(), "primary")
                                    }
                                }
                            }

                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }

                        suspend fun delete(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "titles", titleId.toString())
                                    }
                                }
                            }


                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }
                    }
                }

                inner class Genres {
                    suspend fun create(genre: GenreCreate): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "genres")
                                }
                                setBody(genre)
                            }
                        }


                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            Result.Error(response.body<ApiErrorList>())
                        }
                    }

                    inner class Id(
                        val genreId: Int
                    ) {
                        suspend fun delete(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "genres", genreId.toString())
                                    }
                                }
                            }


                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }
                    }
                }

                inner class Tags {
                    suspend fun create(tag: TagCreate): Result<Unit> {

                        val response = httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "tags")
                                }
                                setBody(tag)
                            }
                        }


                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            Result.Error(response.body<ApiErrorList>())
                        }
                    }

                    inner class Id(
                        val tagId: Int
                    ) {
                        suspend fun delete(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "tags", tagId.toString())
                                    }
                                }
                            }

                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }
                    }
                }

                inner class Sources {
                    suspend fun create(source: SourceCreate): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "sources")
                                }
                                setBody(source)
                            }
                        }


                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            Result.Error(response.body<ApiErrorList>())
                        }
                    }

                    inner class Id(
                        val sourceId: Int
                    ) {
                        suspend fun update(source: Source): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.patch(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "sources", sourceId.toString())
                                    }
                                    setBody(source)
                                }
                            }


                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }

                        suspend fun delete(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "sources", sourceId.toString())
                                    }
                                }
                            }

                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }
                    }

                }

                inner class Logs {
                    suspend fun create(log: LogCreate): Result<Unit> {
                        val response = httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "logs")
                                }
                                setBody(log)
                            }
                        }

                        return if(response.status.isSuccess()) {
                            Result.Success(Unit)
                        } else {
                            Result.Error(response.body<ApiErrorList>())
                        }
                    }

                    inner class Id(
                        val logId: Int
                    ) {
                        suspend fun update(log: Log): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.patch(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "logs", logId.toString())
                                    }
                                    setBody(log)
                                }
                            }


                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }

                        suspend fun delete(): Result<Unit> {
                            val response = httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "logs", logId.toString())
                                    }
                                }
                            }

                            return if(response.status.isSuccess()) {
                                Result.Success(Unit)
                            } else {
                                Result.Error(response.body<ApiErrorList>())
                            }
                        }
                    }
                }
            }
        }
    }
}