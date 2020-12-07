package day12

import common.gcd
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.sign

class Vec(val coords: IntArray) {
    val rank: Int get() = coords.size
    operator fun get(axis: Int) = coords[axis]
    operator fun set(axis: Int, value: Int) { coords[axis] = value }
    fun energy() = coords.sumOf { abs(it) }
    override fun toString(): String = coords.contentToString()
}
operator fun Vec.plusAssign(other: Vec) {
    check(this.rank == other.rank)
    for (n in 0 until rank)
        this[n] += other[n]
}

class AxisPhase(val values: IntArray) {
    override fun equals(other: Any?): Boolean =
            other is AxisPhase && this.values.contentEquals(other.values)
    override fun hashCode(): Int = values.contentHashCode()
    override fun toString(): String = values.contentToString()
}

class Moon(var position: Vec, var velocity: Vec = Vec(IntArray(position.rank))) {
    fun gravitateWith(other: Moon) {
        for(n in 0 until position.rank) {
            this.velocity[n] += (other.position[n] - this.position[n]).sign
        }
    }
    fun move() { position += velocity }
    override fun toString(): String = "pos=$position, vel=$velocity"
    fun totalEnergy() = position.energy() * velocity.energy()
}

fun List<Moon>.axisPhases(): List<AxisPhase> = List(this.first().position.rank) { k ->
    val phase = IntArray(size * 2)
    forEachIndexed { i, m -> phase[i] = m.position[k]; phase[size + i] = m.velocity[k] }
    AxisPhase(phase)
}




fun main() {
    val input = Path("src/day12/input.txt").readLines()
    val numberRegex = Regex("-?\\d+")
    val moons = input.map { Moon(Vec(numberRegex.findAll(it).map { it.value.toInt() }.toList().toIntArray())) }
    val rank = moons.first().position.rank

    val moonPairs = moons.flatMap { a -> moons.mapNotNull { b -> (a to b).takeIf { a != b } } }

    moons.forEach(::println)

    val axisPhases = moons.axisPhases().map { mutableSetOf(it) }
    val cycles = MutableList(rank) { 0 }

    outer@
    for (step in 1..1_000_000) {
        moonPairs.forEach { (a, b) -> a.gravitateWith(b) }
        moons.forEach { it.move() }

        for ((k, phase) in moons.axisPhases().withIndex()) {
            if (!axisPhases[k].add(phase) && cycles[k] == 0) {
                val start = axisPhases[k].indexOf(phase)
                cycles[k] = (step - start).also { period ->
                    println("Axis $k phase cycles at step $step with period $period")
                }
                if (cycles.all { it != 0 } && step > 1000) break@outer
            }
        }
        if (step == 1000) {
            println("Total energy: " + moons.sumOf { it.totalEnergy() })
        }
    }

    moons.forEach(::println)
    println(cycles.map { it.toLong() }.fold(1L) { acc, e -> acc * e / gcd(acc, e) })
}