package day5

import common.IntCodeComputer
import common.loadIntCode

fun IntCodeComputer.runWithInput(input: Int): List<Long> = reset().input(input).runToHaltGetOutputs()
fun runWithInput(program: List<Long>, input: Int): List<Int> =
    IntCodeComputer(program).runWithInput(input).map { it.toInt() }

fun main() {
//    runTests()
    val code = loadIntCode("src/day5/input.txt")
    val computer = IntCodeComputer(code)

    val output1 = computer.runWithInput( 1)
    println(output1)

    val output2 = computer.runWithInput( 5)
    println(output2)
}

fun test1eq8(value: Int): Int = runWithInput(listOf(3,9,8,9,10,9,4,9,99,-1,8), value).single()
fun test1lt8(value: Int): Int = runWithInput(listOf(3,9,7,9,10,9,4,9,99,-1,8), value).single()
fun test2eq8(value: Int): Int = runWithInput(listOf(3,3,1108,-1,8,3,4,3,99), value).single()
fun test2lt8(value: Int): Int = runWithInput(listOf(3,3,1107,-1,8,3,4,3,99), value).single()

fun test1z(value: Int) = runWithInput(listOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9), value).single()
fun test2z(value: Int) = runWithInput(listOf(3,3,1105,-1,9,1101,0,0,12,4,12,99,1), value).single()
fun runTests() {
    println(test1z(0))

    for (i in 1..10) {
        println("$i: 1eq8 = ${test1eq8(i)}, 1lt8 = ${test1lt8(i)}, 2eq8 = ${test2eq8(i)}, 2lt8 = ${test2lt8(i)}")
    }
    for (i in -2..2) {
        println("$i: 1z = ${test1z(i)}, 2z = ${test2z(i)}")
    }
}
