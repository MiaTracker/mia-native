package infrastructure

import coil3.intercept.Interceptor
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageResult
import coil3.size.Dimension
import data_objects.ApiImage
import data_objects.ImageSize
import io.ktor.http.*

class ImageSizeInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val image = request.data as ApiImage

        val (width, height) = chain.size

        val newUri = buildUrl {
            takeFrom(Preferences.instanceUrl ?: throw Exception("InstanceUrl not yet set!"))
            appendPathSegments("img")
            appendPathSegments(
                if(width is Dimension.Pixels && height is Dimension.Pixels) getSlug(image.sizes, width, height)
                else getSlug(image.sizes, Dimension(Int.MAX_VALUE), Dimension(Int.MAX_VALUE)))
            appendPathSegments(image.path)
        }.toString()

        val transformedRequest = request.newBuilder()
            .data(newUri)
            .httpHeaders(
                NetworkHeaders.Builder()
                    .set(HttpHeaders.Authorization, "Bearer ${Preferences.Authorization.token}")
                    .build()
            )
            .build()
        return chain.withRequest(transformedRequest).proceed()
    }

    private fun getSlug(sizes: List<ImageSize>, width: Dimension.Pixels, height: Dimension.Pixels): String {

        val size = sizes.filter { size ->
            size.width >= width.px && size.height >= height.px
        }.minByOrNull { size -> size.width * size.height } ?: sizes.maxBy { size ->
            size.width * size.height
        }

        return size.slug
    }
}