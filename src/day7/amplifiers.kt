package day7

import common.IntCodeComputer
import common.loadIntCode
import common.permutations


fun runWithInput(program: List<Long>, phase: Int, input: Int): List<Long> =
    IntCodeComputer(program).input(phase).input(input).runToHaltGetOutputs()


fun amplifySignal(code: List<Long>, phases: List<Int>) = phases.fold(0) { input, phase ->
    runWithInput(code, phase, input).single().toInt()
}

fun amplifySignal2(code: List<Long>, phases: List<Int>): Long {
    val amplifiers = phases.map { phase -> IntCodeComputer(code).input(phase) }
    amplifiers.first().input(0)

    var lastValue = 0L
    do {
        for (index in amplifiers.indices) {
            val nextIndex = (index + 1) % amplifiers.size
            val amp = amplifiers[index]
            val next = amplifiers[nextIndex]
            amp.runAndConsume { value -> next.input(value); lastValue = value }
        }
    } while (!amplifiers.last().state.isHalted())
    return lastValue
}



val allPhases = (0..4).toList().permutations()
fun maxPhases(code: List<Long>): List<Int> = allPhases.maxByOrNull { phases -> amplifySignal(code, phases) }!!

val allPhases2 = (5..9).toList().permutations()
fun maxPhases2(code: List<Long>): List<Int> = allPhases2.maxByOrNull { phases -> amplifySignal2(code, phases) }!!


fun main() {
    val code = loadIntCode("src/day7/ampCode.txt")

//    runTests()

    val max = maxPhases(code).also(::println)
    amplifySignal(code, max).also(::println)

//    runTests2()

    val max2 = maxPhases2(code).also(::println)
    amplifySignal2(code, max2).also(::println)
}

fun runTests() {
    val code1: List<Long> = listOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
    val max1 = maxPhases(code1).also(::println)
    amplifySignal(code1, max1).also(::println)

    val code2: List<Long> = listOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
    val max2 = maxPhases(code2).also(::println)
    amplifySignal(code2, max2).also(::println)
}

fun runTests2() {
    val code: List<Long> = listOf(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)
    val computer = IntCodeComputer(code)
    computer.input(9).input(0).input(1).runAndConsume { value ->
        println("output: $value")
    }.let { println("Term state: $it") }
    computer.input(1).input(2).runAndConsume { value ->
        println("output: $value")
    }.let { println("Term state: $it") }

    amplifySignal2(code, listOf(9,8,7,6,5)).also(::println)

}
