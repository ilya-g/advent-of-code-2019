package day2

import common.IntCodeComputer
import common.loadIntCode

fun main() {
    val data = loadIntCode("src/day2/input.txt")

    val computer = IntCodeComputer(data)
    println(computer.runNounVerb(12, 2))
    println("------")
    for (noun in 0..99) {
        for (verb in 0..99) {
            val result = computer.reset().runNounVerb(noun, verb)
            if (result == 19690720) {
                println("noun: $noun, verb: $verb, result: $result, output: ${noun * 100 + verb}")
            }
        }
    }
}
