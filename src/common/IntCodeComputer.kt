package common


class IntCodeComputer(initial: List<Int>) {
    val memory = initial.toMutableList()
    var ip = 0
    fun input(value: Int): IntCodeComputer = apply { this.inputs += value }
    private val inputs = mutableListOf<Int>()
    private var output: Int? = null
    lateinit var state: CommandResult
        private set

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

    enum class CommandResult {
        Continue,
        WaitInput,
        Halt;
        fun isRunning() = this == Continue
        fun isHalted() = this == Halt
    }

    fun runAndConsume(output: (value: Int) -> Unit): CommandResult {
        do {
            state = advance()
            this.output?.let(output)
        } while (state.isRunning())
        return state
    }

    fun runNounVerb(noun: Int, verb: Int): Int {
        memory[1] = noun
        memory[2] = verb
        runAndConsume { /* output ignored */ }.ensureNotWaiting()
        return memory[0]
    }

    fun runToHaltGetOutputs(): List<Int> = buildList {
        runAndConsume { add(it) }.ensureNotWaiting()
    }

    private fun CommandResult.ensureNotWaiting(): CommandResult = apply {
        if (this == CommandResult.WaitInput) error("Should not stop to wait at IP:$ip")
    }

    private val immediateMasks = listOf(100, 1000, 10000)
    private fun isImmediate(opCode: Int, argNo: Int) = (opCode / immediateMasks[argNo]) % 10 != 0
    private fun readNext(): Int = memory[ip++]
    private fun advance(): CommandResult {
        output = null
        val ip_ = ip
        fun restoreIp() { ip = ip_ }
        val opCode = readNext()
        fun readArg(argNo: Int) = if (isImmediate(opCode, argNo)) readNext() else memory[readNext()]
        fun writeArg(argNo: Int, value: Int) { check(!isImmediate(opCode, argNo)); memory[readNext()] = value }


        when (val op = opCode % 100) {
            OpCodes.HALT -> return CommandResult.Halt
            OpCodes.ADD -> (readArg(0) + readArg(1)).let { writeArg(2, it) }
            OpCodes.MUL -> (readArg(0) * readArg(1)).let { writeArg(2, it) }
            OpCodes.INP -> {
                val input = inputs.removeFirstOrNull() ?: return CommandResult.WaitInput.also { restoreIp() }
                writeArg(0, input)
            }
            OpCodes.OUT -> output = readArg(0)
            OpCodes.JT -> { val v = readArg(0); val t = readArg(1); if (v != 0) ip = t }
            OpCodes.JF -> { val v = readArg(0); val t = readArg(1); if (v == 0) ip = t }
            OpCodes.LT -> (readArg(0) < readArg(1)).let { writeArg(2, if (it) 1 else 0) }
            OpCodes.EQ -> (readArg(0) == readArg(1)).let { writeArg(2, if (it) 1 else 0) }
            else -> println("DEBUG: Unknown opcode $op")
        }
        return CommandResult.Continue
    }
}
