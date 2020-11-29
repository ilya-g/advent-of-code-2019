package day2

import common.IntCodeComputer
import kotlin.io.path.*

fun main() {
    val data = Path("src/day2/input.txt").readText().trim().split(",").map { it.toInt() }

    println(IntCodeComputer(data).runNounVerb(12, 2))
    println("------")
    for (noun in 0..99) {
        for (verb in 0..99) {
            val result = IntCodeComputer(data).runNounVerb(noun, verb)
            if (result == 19690720) {
                println("noun: $noun, verb: $verb, result: $result, output: ${noun * 100 + verb}")
            }
        }
    }
}
