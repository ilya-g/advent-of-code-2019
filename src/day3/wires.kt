package day3

import common.positionXY.Direction
import common.positionXY.Pos
import common.splitAt
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

data class Move(val direction: Direction, val steps: Int) {
    override fun toString(): String = "$direction$steps"
    companion object {
        fun parse(s: String): Move =
            s.splitAt(1).let { (d, s) -> Move(Direction.valueOf(d), s.toInt()) }
    }
}

fun List<Move>.follow(): Sequence<Pos> = sequence {
    var x = 0
    var y = 0
    this@follow.forEach { (direction, steps) ->
        repeat(steps) {
            x += direction.dx
            y += direction.dy
            yield(Pos(x, y))
        }
    }
}

fun List<Move>.toPointSet(): Set<Pos> = follow().toSet()
fun List<Move>.toStepDistances(): Map<Pos, Int> = buildMap {
    var step = 1
    follow().forEach { pos ->
        putIfAbsent(pos, step++)
    }
}

fun Pos.distance(): Int = abs(x) + abs(y)


fun main() {
    val input = Path("src/day3/input.txt").readLines().filter { it.isNotEmpty() }
    val wireMoves = input.map { it.split(",").map(Move::parse) }.onEach(::println)
    val wirePositions = wireMoves.map { it.toPointSet() }
    println("intersections")
    val crossPoints = wirePositions.let { (wp1, wp2) -> wp1 intersect wp2 }.onEach(::println)
    val closestPoint = crossPoints.minByOrNull { it.distance() }!!
    println("Closest: $closestPoint at distance ${closestPoint.distance()}")

    val wirePointsSteps = wireMoves.map { it.toStepDistances() }
    println("pos:    steps wire 1, steps wire 2")
    crossPoints.forEach { p -> println("$p: ${wirePointsSteps[0][p]}, ${wirePointsSteps[1][p]}") }
    val soonestCrossSteps = crossPoints.minOf { p -> wirePointsSteps.sumOf { it.getValue(p) } }
    soonestCrossSteps.let(::println)
}


