package day9

import common.IntCodeComputer
import common.loadIntCode


fun main() {
    val code = loadIntCode("src/day9/input.txt")
    val computer = IntCodeComputer(code)

    computer.reset().input(1).runToHaltGetOutputs().let(::println)
    computer.reset().input(2).runToHaltGetOutputs().let(::println)
//    runTests()
}

fun runTests() {
    val code: List<Long> = listOf(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
    IntCodeComputer(code).runToHaltGetOutputs().let(::println)
    val code2: List<Long> = listOf(104,1125899906842624,99)
    IntCodeComputer(code2).runToHaltGetOutputs().let(::println)
}
