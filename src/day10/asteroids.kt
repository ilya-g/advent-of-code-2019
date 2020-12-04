package day10

import common.positionXY.Pos
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.sign

tailrec fun gcd(a: Int, b: Int): Int =
        if (b == 0) a else gcd(b, a % b)

operator fun Pos.minus(other: Pos) = Delta(this.x - other.x, this.y - other.y)
operator fun Pos.plus(delta: Delta) = Pos(this.x + delta.dx, this.y + delta.dy)
operator fun Delta.times(n: Int) = Delta(dx * n, dy * n)

data class Delta(val dx: Int, val dy: Int) {
    val m = abs(gcd(dx, dy))
    fun normalized() = if (m <= 1) this else Delta(dx / m, dy / m)
    override fun toString(): String = "D($dx;$dy)"
}



fun <T> List<List<T>>.collate(): Sequence<T> = sequence {
    val temp = this@collate.toMutableList()
    var position = 0
    do {
        temp.removeAll { position >= it.size }
        yieldAll(temp.map { it[position] })
        position += 1
    } while (temp.isNotEmpty())
}

fun main() {
    val rows = Path("src/day10/input.txt").readLines()

    angleComparatorTests()

    // TODO: Boolean.ifTrue ( () -> R ): R?
    // TODO: partition + map
    val asteroids = rows.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell -> if (cell == '#') Pos(x, y) else null }
    }.toSet()

    fun Pos.isVisibleFrom(station: Pos): Boolean {
        val d = this - station
        val nd = d.normalized()
        return (d.m > 0 && (1 until d.m).none { k -> (station + nd * k) in asteroids }) //.also { println(it) }
    }
    fun countVisibleFrom(pos: Pos): Int = asteroids.count { it.isVisibleFrom(pos) }

    fun countVisibleFrom2(pos: Pos): Int = asteroids.distinctBy { (it - pos).normalized() }.size - 1

    println("Total asteroids: ${asteroids.size}")

    val maxPos = asteroids.maxByOrNull { countVisibleFrom2(it) }!!
    println(maxPos)
    println(asteroids.maxOf { countVisibleFrom(it) })
    println(countVisibleFrom2(maxPos))


    val relatives = (asteroids - maxPos).map { it - maxPos }
            .groupBy { it.normalized() }
            .mapValues { (_, v) -> v.sortedBy { it.m } }
            .entries
            .sortedWith(compareBy(angleComparator) { it.key })

    val vaporizeOrder = relatives.map { it.value }.collate().map { maxPos + it }.toList()
    vaporizeOrder.forEachIndexed { i, p ->
        println("${i + 1}: $p")
    }
    println("========")
    val result = vaporizeOrder[200 - 1]
    println("200th: ${result}")
    println(result.x * 100 + result.y)

}


val angleComparator = compareBy<Delta> { if (it.dx >= 0) 1 else 2 }
        .then { a, b ->
            when {
                a.dy == b.dy -> -a.dx.compareTo(b.dx) * a.dy.sign
                a.dx == b.dx || a.dy == 0 || b.dy == 0 -> a.dy.compareTo(b.dy) * if (a.dx >= 0) 1 else -1
                else -> {
                    val aby = -a.dy * b.dy
                    (a.dx * aby / a.dy).compareTo(b.dx * aby / b.dy)
                }
            }
        }

fun angleComparatorTests() {
    val cmp = angleComparator
    fun d(x: Int, y: Int) = Delta(x, y)
    val ds = listOf(
            /* Q1: */ d(0, -1), d(1, -100), d(1, -1), d(2, -1), d(2, 0),
            /* Q2: */ d(2, 1), d(1, 1), d(2, 10), d(1, 10), d(0, 1),
            /* Q3: */ d(-1, 10), d(-1, 1), d(-1, 0),
            /* Q4: */ d(-10, -1), d(-1, -1), d(-1, -10),
    )

    repeat(10000) {
        val i1 = ds.indices.random()
        val i2 = ds.indices.random()
        val a = ds[i1]
        val b = ds[i2]
        if (cmp.compare(a, b).sign != i1.compareTo(i2).sign)
            println("$a $b: ${cmp.compare(a, b)} == ${i1.compareTo(i2)}")
    }
//    exitProcess(0)
}
