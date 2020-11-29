package common


class IntCodeComputer(initial: List<Int>) {
    val memory = initial.toMutableList()
    var ip = 0
    fun input(value: Int): IntCodeComputer = apply { this.inputs += value }
    private val inputs = mutableListOf<Int>()
    private var output: Int? = null

    object OpCodes {
        const val ADD = 1
        const val MUL = 2
        const val INP = 3
        const val OUT = 4
        const val JT = 5
        const val JF = 6
        const val LT = 7
        const val EQ = 8
        const val HALT = 99
    }

    fun runNounVerb(noun: Int, verb: Int): Int {
        memory[1] = noun
        memory[2] = verb
        while (advance()) { /* work */ }
        return memory[0]
    }

    fun runOutputs(): List<Int> = buildList {
        while (advance()) {
            output?.let { add(it) }
        }
    }

    private val immediateMasks = listOf(100, 1000, 10000)
    private fun isImmediate(opCode: Int, argNo: Int) = (opCode / immediateMasks[argNo]) % 10 != 0
    private fun readNext(): Int = memory[ip++]
    private fun advance(): Boolean {
        output = null
        val opCode = readNext()
        fun readArg(argNo: Int) = if (isImmediate(opCode, argNo)) readNext() else memory[readNext()]
        fun writeArg(argNo: Int, value: Int) { check(!isImmediate(opCode, argNo)); memory[readNext()] = value }

        when (val op = opCode % 100) {
            OpCodes.HALT -> return false
            OpCodes.ADD -> (readArg(0) + readArg(1)).let { writeArg(2, it) }
            OpCodes.MUL -> (readArg(0) * readArg(1)).let { writeArg(2, it) }
            OpCodes.INP -> writeArg(0, inputs.removeFirstOrNull() ?: error("No input for read at IP:$ip"))
            OpCodes.OUT -> output = readArg(0)
            OpCodes.JT -> { val v = readArg(0); val t = readArg(1); if (v != 0) ip = t }
            OpCodes.JF -> { val v = readArg(0); val t = readArg(1); if (v == 0) ip = t }
            OpCodes.LT -> (readArg(0) < readArg(1)).let { writeArg(2, if (it) 1 else 0) }
            OpCodes.EQ -> (readArg(0) == readArg(1)).let { writeArg(2, if (it) 1 else 0) }
            else -> println("DEBUG: Unknown opcode $op")
        }
        return true
    }
}
