package helpers

import java.util.stream.Stream
import kotlin.jvm.optionals.getOrDefault

fun <T> Stream<T>.append(vararg params: T): Stream<T> = Stream.concat(this, Stream.of(*params))

fun <T> Stream<T>.max(): T? where T : Comparable<T> = this.max { x, y -> x.compareTo(y) }.getOrDefault(null)