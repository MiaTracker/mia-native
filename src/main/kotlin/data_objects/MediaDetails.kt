package data_objects

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

sealed interface MediaDetails {
    val id: Int
    val posterPath: String?
    val backdropPath: String?
    val stars: Float?
    val title: String
    val alternativeTitles: List<AlternativeTitle>
    val status: String?
    val overview: String?
    val tmdbVoteAverage: Float?
    val onWatchlist: Boolean
    val originalLanguage: Language?
    val genres: List<Genre>
    val tags: List<Tag>
    val sources: List<Source>
    val logs: List<Log>
}

@Serializable
data class MovieDetails(
    override val id: Int,
    override val posterPath: String?,
    override val backdropPath: String?,
    override val stars: Float?,
    override val title: String,
    override val alternativeTitles: List<AlternativeTitle>,
    val releaseDate: LocalDate?,
    val runtime: Int?,
    override val status: String?,
    override val overview: String?,
    override val tmdbVoteAverage: Float?,
    override val onWatchlist: Boolean,
    override val originalLanguage: Language?,
    override val genres: List<Genre>,
    override val tags: List<Tag>,
    override val sources: List<Source>,
    override val logs: List<Log>
) : MediaDetails

@Serializable
data class SeriesDetails(
    override val id: Int,
    override val posterPath: String?,
    override val backdropPath: String?,
    override val stars: Float?,
    override val title: String,
    override val alternativeTitles: List<AlternativeTitle>,
    val firstAirDate: LocalDate?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    override val status: String?,
    val type: String?,
    override val overview: String?,
    override val tmdbVoteAverage: Float?,
    override val onWatchlist: Boolean,
    override val originalLanguage: Language?,
    override val genres: List<Genre>,
    override val tags: List<Tag>,
    override val sources: List<Source>,
    override val logs: List<Log>
) : MediaDetails