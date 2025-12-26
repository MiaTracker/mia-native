import data_objects.*
import infrastructure.Preferences
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object Api {
    val instance: Instance = Instance()

    @OptIn(ExperimentalSerializationApi::class)
    private fun httpClient(): HttpClient = platformHttpClient()
        .config {
            install(ContentNegotiation) {
                json(
                    Json {
                        namingStrategy = JsonNamingStrategy.SnakeCase
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 60000
            }

            defaultRequest {
                contentType(ContentType.Application.Json)

                Preferences.Authorization.token?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }

    suspend fun ping(instance: String): Boolean =
        try {
            val response = httpClient().use { client ->
                client.get(instance) {
                    url {
                        appendPathSegments("ping")
                    }
                }
            }.parse<PingResponse>()

            when(response) {
                is Result.Error<*> -> false
                is Result.Success<PingResponse> -> response.value.status == PingStatus.Up
            }
        } catch(_: Exception) {
            false
        }

    class Instance {
        private val baseUrl: String
            get() = Preferences.instanceUrl
                ?: throw Exception("InstanceUrl not yet set!")

        inner class Users {
            suspend fun index(): Result<List<UserProfile>> =
                httpClient().use { client ->
                    client.get(baseUrl) {
                        url {
                            appendPathSegments("users")
                        }
                    }
                }.parse()

            suspend fun login(request: LoginRequest): Result<LoginResult> =
                httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments("users", "login")
                        }
                        setBody(request)
                    }
                }.parse()

            suspend fun profile(): Result<UserProfile> =
                httpClient().use { client ->
                    client.get(baseUrl) {
                        url {
                            appendPathSegments("users", "profile")
                        }
                    }
                }.parse()

            suspend fun changePassword(change: PasswordChange): Result<Unit> =
                httpClient().use { client ->
                    client.patch(baseUrl) {
                        url {
                            appendPathSegments("users", "password")
                        }

                        setBody(change)
                    }
                }.parse()

            suspend fun register(user: UserRegister): Result<Unit> =
                httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments("users", "register")
                        }

                        setBody(user)
                    }
                }.parse()

            suspend fun delete(uuid: String): Result<Unit> =
                httpClient().use { client ->
                    client.delete(baseUrl) {
                        url {
                            appendPathSegments("users", uuid)
                        }
                    }
                }.parse()
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

        inner class Watchlist : BaseMediaApi() {
            override val subpath: String
                get() = "watchlist"

            suspend fun add(mediaId: Int): Result<Unit> =
                httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments(subpath, "add")
                        }

                        setBody(
                            WatchlistChangeBody(
                                mediaId = mediaId
                            )
                        )
                    }
                }.parse()

            suspend fun remove(mediaId: Int): Result<Unit> =
                httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments(subpath, "remove")
                        }

                        setBody(
                            WatchlistChangeBody(
                                mediaId = mediaId
                            )
                        )
                    }
                }.parse()

        }

        abstract inner class BaseMediaApi {
            protected abstract val subpath: String

            suspend fun index(offset: Int? = null, limit: Int? = null): Result<List<InternalMediaIndex>> =
                httpClient().use { client ->
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
                }.parse()


            suspend fun search(
                query: String,
                committed: Boolean,
                offset: Int? = null,
                limit: Int? = null
            ): Result<SearchResults> =
                httpClient().use { client ->
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
                }.parse()
        }

        abstract inner class BaseMediaInstanceApi<T: MediaDetails> : BaseMediaApi() {
            protected abstract val type: TypeInfo

            suspend fun create(externalId: Int): Result<Int> =
                httpClient().use { client ->
                    client.post(baseUrl) {
                        url {
                            appendPathSegments(subpath)
                            parameters.append("tmdb_id", externalId.toString())
                        }
                    }
                }.parse()


            inner class Id(
                private val mediaId: Int
            ) {
                suspend fun get(): Result<T> =
                    httpClient().use { client ->
                        client.get(baseUrl) {
                            url {
                                appendPathSegments(subpath, mediaId.toString())
                            }
                        }
                    }.parse<T>(type)


                inner class Titles {

                    suspend fun create(title: TitleCreate): Result<Unit> =
                        httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "titles")
                                }
                                setBody(title)
                            }
                        }.parse()

                    inner class Id(
                        private val titleId: Int
                    ) {
                        suspend fun primary(): Result<Unit> =
                            httpClient().use { client ->
                                client.post(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "titles", titleId.toString(), "primary")
                                    }
                                }
                            }.parse()

                        suspend fun delete(): Result<Unit> =
                            httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "titles", titleId.toString())
                                    }
                                }
                            }.parse()
                    }
                }

                inner class Genres {
                    suspend fun create(genre: GenreCreate): Result<Unit> =
                        httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "genres")
                                }
                                setBody(genre)
                            }
                        }.parse()


                    inner class Id(
                        val genreId: Int
                    ) {
                        suspend fun delete(): Result<Unit> =
                            httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "genres", genreId.toString())
                                    }
                                }
                            }.parse()
                    }
                }

                inner class Tags {
                    suspend fun create(tag: TagCreate): Result<Unit> =
                        httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "tags")
                                }
                                setBody(tag)
                            }.parse()
                        }

                    inner class Id(
                        val tagId: Int
                    ) {
                        suspend fun delete(): Result<Unit> =
                            httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "tags", tagId.toString())
                                    }
                                }
                            }.parse()
                    }
                }

                inner class Sources {
                    suspend fun create(source: SourceCreate): Result<Unit> =
                        httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "sources")
                                }
                                setBody(source)
                            }
                        }.parse()

                    inner class Id(
                        val sourceId: Int
                    ) {
                        suspend fun update(source: Source): Result<Unit> =
                            httpClient().use { client ->
                                client.patch(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "sources", sourceId.toString())
                                    }
                                    setBody(source)
                                }
                            }.parse()


                        suspend fun delete(): Result<Unit> =
                            httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "sources", sourceId.toString())
                                    }
                                }
                            }.parse()
                    }

                }

                inner class Logs {
                    suspend fun create(log: LogCreate): Result<Unit> =
                        httpClient().use { client ->
                            client.post(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "logs")
                                }
                                setBody(log)
                            }
                        }.parse()

                    inner class Id(
                        val logId: Int
                    ) {
                        suspend fun update(log: Log): Result<Unit> =
                            httpClient().use { client ->
                                client.patch(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "logs", logId.toString())
                                    }
                                    setBody(log)
                                }
                            }.parse()

                        suspend fun delete(): Result<Unit> =
                            httpClient().use { client ->
                                client.delete(baseUrl) {
                                    url {
                                        appendPathSegments(subpath, mediaId.toString(), "logs", logId.toString())
                                    }
                                }
                            }.parse()
                    }
                }

                inner class Backdrops {
                    suspend fun index(): Result<List<ImageCandidate>> =
                        httpClient().use { client ->
                            client.get(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "backdrops")
                                }
                            }
                        }.parse()

                    suspend fun default(backdrop: BackdropUpdate): Result<Unit> =
                        httpClient().use { client ->
                            client.patch(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "backdrops", "default")
                                }

                                setBody(backdrop)
                            }
                        }.parse()
                }

                inner class Posters {
                    suspend fun index(): Result<List<ImageCandidate>> =
                        httpClient().use { client ->
                            client.get(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "posters")
                                }
                            }
                        }.parse()


                    suspend fun default(poster: PosterUpdate): Result<Unit> =
                        httpClient().use { client ->
                            client.patch(baseUrl) {
                                url {
                                    appendPathSegments(subpath, mediaId.toString(), "posters", "default")
                                }

                                setBody(poster)
                            }
                        }.parse()
                }
            }
        }

        suspend fun statistics(): Result<Stats> =
            httpClient().use { client ->
                client.get(baseUrl) {
                    url {
                        appendPathSegments("statistics")
                    }
                }
            }.parse()
    }
}

suspend inline fun<reified T> HttpResponse.parse(): Result<T> = parse(typeInfo<T>())

suspend inline fun<T> HttpResponse.parse(type: TypeInfo): Result<T> {
    try {
        return if(this.status.isSuccess()) {
            if(type == Unit::class) {
                @Suppress("UNCHECKED_CAST")
                Result.Success(Unit as T)
            }
            else {
                Result.Success(this.body<T>(type))
            }
        } else {
            Result.Error(
                Errors.ApiErrors(
                    status = this.status,
                    apiErrors = this.body<ApiErrorList>()
                )
            )
        }
    } catch (ex: Exception) {
        println("Exception parsing response: " + ex.message)
        return Result.Error(Errors.CaughtException(ex))
    }
}

expect fun platformHttpClient(): HttpClient
