package extensions

fun Int.toString(places: Int): String {
    val builder = StringBuilder()
    var i = this
    var places = places
    while(i != 0 && places != 0) {
        builder.append(i % 10)
        i /= 10
        places--
    }
    return builder.toString()
}