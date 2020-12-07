package common

tailrec fun gcd(a: Int, b: Int): Int =
        if (b == 0) a else gcd(b, a % b)
tailrec fun gcd(a: Long, b: Long): Long =
        if (b == 0L) a else gcd(b, a % b)


fun Int.multipliers() = sequence {
    var n = this@multipliers
    var m = 2
    while (m * m < n) {
        while (n % m == 0 && m * m < n) {
            n /= m
            yield(m)
        }
        m += 1 + (m and 1)
    }
    if (n != 1) yield(n)
}.groupingBy { it }.eachCount()