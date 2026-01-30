package extensions

fun Int.toString(places: Int): String {
    var builder = ""
    var i = this
    var places = places
    while(i != 0 || places != 0) {
        builder = (i % 10).toString() + builder
        i /= 10
        places--
    }
    return builder
}