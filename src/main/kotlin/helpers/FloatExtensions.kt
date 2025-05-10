package helpers

import kotlin.math.roundToInt

fun Float.toStarsString(): String {
    val rounded = (this * 100.0).roundToInt()

    return if(rounded % 100 == 0) (rounded / 100).toString()
    else (rounded / 100.0).toString()
}