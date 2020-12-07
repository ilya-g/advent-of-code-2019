package day11

import common.IntCodeComputer
import common.loadIntCode
import common.positionXY.Pos
import common.positionXY.Direction

fun main() {
    val code = loadIntCode("src/day11/input.txt")
    val computer = IntCodeComputer(code)

    val field = mutableMapOf<Pos, Int>()

    // for part 2
    field[Pos(0, 0)] = 1

    var pos = Pos(0,0)
    var direction = Direction.U
    do {
        val (color, turn) =
                computer.input(field.getOrElse(pos) { 0 }).runToPauseGetOutputs()
                        .also { check(it.size == 2) }
//        println("${computer.state} $color $turn")
        field[pos] = color.toInt()
        direction = if (turn == 1L) direction.turnRight() else direction.turnLeft()
        pos = Pos(pos.x + direction.dx,pos.y + direction.dy)
    } while (!computer.state.isHalted())
    println(field.size)
    val xs = field.keys.run { minOf { it.x }..maxOf { it.x } }
    val ys = field.keys.run { minOf { it.y }..maxOf { it.y } }
    println("x: $xs")
    println("y: $ys")

    for (y in ys.reversed()) {
        for (x in xs) {
            print(if (field[Pos(x, y)] == 1) "## " else "   ")
        }
        println()
    }
}