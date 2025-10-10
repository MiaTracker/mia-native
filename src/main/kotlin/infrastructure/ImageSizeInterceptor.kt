package infrastructure

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.size.Dimension
import data_objects.ImageSizeDimension
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom

class ImageSizeInterceptor(val imageType: ImageType) : Interceptor {
    enum class ImageType {
        Poster,
        Backdrop
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val uri = request.data as String

        val (width, height) = chain.size

        val newUri = buildUrl {
            takeFrom(Configuration.images.secureBaseUrl)
            appendPathSegments(
                if(width is Dimension.Pixels && height is Dimension.Pixels) getSlug(width, height)
                else getSlug(Dimension(Int.MAX_VALUE), Dimension(Int.MAX_VALUE)))
            appendPathSegments(uri)
        }.toString()

        val transformedRequest = request.newBuilder()
            .data(newUri)
            .build()
        return chain.withRequest(transformedRequest).proceed()
    }

    private fun getSlug(width: Dimension.Pixels, height: Dimension.Pixels): String {
        val config = Configuration.images

        val sizes = when(imageType) {
            ImageType.Poster -> config.posterSizes
            ImageType.Backdrop -> config.backdropSizes
        }

        val size = sizes.filter { size ->
            size.size == null || when(size.dimension) {
                ImageSizeDimension.Width -> size.size >= width.px
                ImageSizeDimension.Height -> size.size >= height.px
            }
        }.minBy { size -> size.size ?: Int.MAX_VALUE }

        return size.slug
    }
}