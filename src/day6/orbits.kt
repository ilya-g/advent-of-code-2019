package day6

import kotlin.io.path.*
import kotlin.time.measureTime

class Orbit(val name: String) {
    var parent: Orbit? = null
    val satellites: MutableList<Orbit> = mutableListOf()

    override fun toString(): String {
        return "$name)${satellites.joinToString(",") { it.name }}"
    }
}


fun main() {
    val data = Path("src/day6/input.txt").readLines().filter { it.isNotEmpty() }

    val orbits = mutableMapOf<String, Orbit>()
    fun orbit(name: String) = orbits.getOrPut(name) { Orbit(name) }

    val COM = orbit("COM")

    data.forEach {
        val (center, satellite) = it.split(")")
        orbit(center).satellites += orbit(satellite)
        orbit(satellite).parent = orbit(center)
    }
    orbits.values.forEach(::println)

    fun totalOrbits(): Int {
        fun sumOrbits(orbit: Orbit, level: Int): Int =
                orbit.satellites.sumOf { level + sumOrbits(it, level + 1) }
        return sumOrbits(COM, 1)
    }

    println("Total orbits: ${totalOrbits()}")

    fun Orbit.pathTo(orbit: Orbit): List<Orbit>? = when {
        this == orbit -> listOf()
        else -> satellites
                .mapNotNull { it.pathTo(orbit) }
                .firstOrNull()
                ?.let { listOf(this) + it }
    }

    fun Orbit.pathToCOM(): List<Orbit> = generateSequence(parent) { it.parent }.toList()

    measureTime {
//        val pathToSAN = COM.pathTo(orbit("SAN"))!! //.also(::println)
//        val pathToYOU = COM.pathTo(orbit("YOU"))!! //.also(::println)
        val pathToSAN = orbit("SAN").pathToCOM().asReversed().also(::println)
        val pathToYOU = orbit("YOU").pathToCOM().asReversed().also(::println)

        val commonPath = (1 until minOf(pathToSAN.size, pathToYOU.size)).first { i -> pathToSAN[i] != pathToYOU[i] }
        val transfers = pathToSAN.size + pathToYOU.size - commonPath * 2
        println("Transfers YOU->SAN: $transfers")
    }.let { println("taken: $it")}

}
