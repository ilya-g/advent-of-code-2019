package day4

fun main() {
    val range = 138241..674034
    println(range.count { it.meetsCriteria() })
    println(range.count { it.meetsCriteria2() })
}

fun Pair<Char, Char>.sameDigits(): Boolean = first == second
fun Pair<Char, Char>.ascending(): Boolean = first <= second

fun Int.meetsCriteria(): Boolean {
    val digitPairs = toString().zipWithNext()
    return digitPairs.all { it.ascending() } && digitPairs.any { it.sameDigits() }
}

// groupRuns could help
fun String.runs(): List<Int> = mutableListOf(length).also { result ->
    var c = first()
    var r = 0
    forEach { c1 ->
        if (c != c1) {
            result.add(r)
            c = c1
            r = 0
        }
        r++
    }
    result.add(r)
}

fun Int.meetsCriteria2(): Boolean {
    val digits = toString()
    val digitPairs = digits.zipWithNext()
    return digitPairs.all { it.ascending() } && digits.runs().any { it == 2 }
}
