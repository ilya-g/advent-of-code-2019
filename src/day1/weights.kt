package day1

import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun fuelForMass(mass: Int): Int =
    (mass / 3 - 2).coerceAtLeast(0)

fun totalFuelForMass(mass: Int): Int =
    generateSequence(fuelForMass(mass), ::fuelForMass)
        .takeWhile { it > 0 }
//        .onEach { println(it) }
        .sum()

fun main() {
//    totalFuelForMass(100756).let(::println)


    val masses = Path("src/day1/input.txt").readLines().map { it.toInt() }

    masses.sumOf(::fuelForMass).also(::println)
    masses.sumOf(::totalFuelForMass).also(::println)

}
