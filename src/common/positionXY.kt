package common.positionXY

data class Pos(val x: Int, val y: Int) {
    override fun toString() = "(x:$x, y:$y)"
}

enum class Direction(val dx: Int, val dy: Int) {
    U(0, 1),
    R(1, 0),
    D(0, -1),
    L(-1, 0);

    companion object {
        private val values = values().toList()

    }
    fun turnRight(): Direction = values[(this.ordinal + 1) % values.size]
    fun turnLeft(): Direction = values[(this.ordinal - 1 + values.size) % values.size]
}