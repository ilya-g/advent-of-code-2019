package day8

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val data = Path("src/day8/input.txt").readText()
    println(data.length)
    val h = 6
    val w = 25
    val layers = data.chunked(w * h) { it.toString().toCharArray() }
    println("Layers: ${layers.size}")

    val minZeroLayer = layers.minByOrNull { it.count { c -> c == '0' } }!!
    println(minZeroLayer.count { it == '1' } * minZeroLayer.count { it == '2' })

    fun List<CharArray>.combined(): CharArray =
            CharArray(first().size) { index -> first { l -> l[index] != '2' }[index] }

    val result = layers.combined()
    for (r in 0 until h) {
        println(result.copyOfRange(r * w, (r + 1) * w).joinToString("") { if (it == '1') "###" else "   "})
    }
}