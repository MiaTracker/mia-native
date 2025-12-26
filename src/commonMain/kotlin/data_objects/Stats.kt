package data_objects

import kotlinx.serialization.Serializable

@Serializable
data class Stats(
    val media: MediaStats,
    val logs: LogStats,
    val genres: List<ComparativeStats>,
    val languages: List<ComparativeStats>,
    val mostWatched: CategoryStats,
    val highestRated: CategoryStats,
    val averageRating: AvgRatingStats
)

@Serializable
data class MediaStats(
    val count: UInt,
    val movies: UInt,
    val series: UInt
)

@Serializable
data class LogStats(
    val logs: UInt,
    val completed: UInt,
    val uncompleted: UInt
)

@Serializable
data class CategoryStats(
    val movie: InternalMediaIndex?,
    val series: InternalMediaIndex?
)

@Serializable
data class ComparativeStats(
    val name: String,
    val count: Int
)

@Serializable
data class AvgRatingStats(
    val overall: Float?,
    val movies: Float?,
    val series: Float?
)