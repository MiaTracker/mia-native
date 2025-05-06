package data_objects

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    val id: Int,
    val posterPath: String?,
    val backdropPath: String?,
    val stars: Float?,
    val title: String,
    val alternativeTitles: List<AlternativeTitle>,
    val releaseDate: LocalDate,
    val runtime: Int?,
    val status: String?,
    val overview: String?,
    val tmdbVoteAverage: Float?,
    val onWatchlist: Boolean,
    val originalLanguage: Language?,
    val genres: List<Genre>,
    val tags: List<Tag>,
    val sources: List<Source>,
    val logs: List<Log>
)