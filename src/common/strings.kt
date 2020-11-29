package common

fun String.splitAt(position: Int): Pair<String, String> {
    check(position in 0..length)
    return substring(0, position) to substring(position)
}
