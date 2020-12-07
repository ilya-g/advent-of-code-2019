package day13

import common.IntCodeComputer
import common.loadIntCode
import common.positionXY.Pos
import kotlin.math.sign

object Tile {
    const val Wall = 1
    const val Block = 2
    const val Pad = 3
    const val Ball = 4
}

fun main() {
    val code = loadIntCode("src/day13/input.txt")
    val computer = IntCodeComputer(code)

    val field = mutableMapOf<Pos, Int>()

    computer.runToHaltGetOutputs().map { it.toInt() }.chunked(3) { (x, y, t) ->
        field[Pos(x, y)] = t
    }
    printField(field)
    println("Total blocks: " + field.values.count { it == Tile.Block })
    Thread.sleep(3000)

    computer.reset()
    field.clear()
    computer.writeMem(0, 2)
    var score = 0
    var prevScore = 0
    var ballX = 0
    var padX = 0
    var time = 0
    do {
        computer.runToPauseGetOutputs().map { it.toInt() }.chunked(3) { (x, y, t) ->
            if (x == -1 && y == 0) score = t else {
                field[Pos(x, y)] = t
                when (t) {
                    Tile.Pad -> padX = x
                    Tile.Ball -> ballX = x
                }
            }
        }

        computer.input((ballX - padX).sign)

        if (time < 50 || prevScore != score) {
            printField(field)
            println("Score: $score, time: $time, ball: $ballX, pad: $padX")
            Thread.sleep(500)
        }
        time++
        prevScore = score
    } while (computer.state.isHalted().not())

}

private fun printField(field: MutableMap<Pos, Int>) {
    val xs = field.keys.run { minOf { it.x }-2..maxOf { it.x }+2 }
    val ys = field.keys.run { minOf { it.y }-1..maxOf { it.y }+1 }

    ys.joinToString("\n") { y ->
        xs.joinToString("") { x ->
            when (field[Pos(x, y)]) {
                Tile.Wall ->    "\u2588\u2588"
                Tile.Block ->   "\u2591\u2591"
                Tile.Pad ->     "__"
                Tile.Ball ->    "()"
                else ->         "  "
            }
        }
    }.let(::println)
}