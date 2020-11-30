package common


class IntCodeComputer(initial: List<Long>) {
    private val rom = initial
    private val ram = mutableMapOf<Int, Long>()
    private var ip = 0  // instruction pointer
    private var bp = 0  // base pointer
    fun input(value: Int): IntCodeComputer = apply { this.inputs += value.toLong() }
    fun input(value: Long): IntCodeComputer = apply { this.inputs += value }
    private val inputs = mutableListOf<Long>()
    private var output: Long? = null
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
        const val SBP = 9
        const val HALT = 99
    }

    enum class CommandResult {
        Continue,
        WaitInput,
        Halt;
        fun isRunning() = this == Continue
        fun isHalted() = this == Halt
    }

    fun reset(): IntCodeComputer = apply {
        inputs.clear()
        ram.clear()
        ip = 0
        bp = 0
        state = CommandResult.Continue
    }

    fun runAndConsume(output: (value: Long) -> Unit): CommandResult {
        do {
            state = advance()
            this.output?.let(output)
        } while (state.isRunning())
        return state
    }

    fun runNounVerb(noun: Int, verb: Int): Int {
        writeMem(1,  noun.toLong())
        writeMem(2, verb.toLong())
        runAndConsume { /* output ignored */ }.ensureNotWaiting()
        return readMem(0).toInt()
    }

    fun runToHaltGetOutputs(): List<Long> = buildList {
        runAndConsume { add(it) }.ensureNotWaiting()
    }

    private fun CommandResult.ensureNotWaiting(): CommandResult = apply {
        if (this == CommandResult.WaitInput) error("Should not stop to wait at IP:$ip")
    }

    private fun readMem(address: Long): Long = when {
        address < 0 -> error("Invalid read address: $address")
        address < rom.size -> ram[address.toInt()] ?: rom[address.toInt()]
        address < Int.MAX_VALUE -> ram[address.toInt()] ?: 0
        else -> error("Invalid read address: $address")
    }

    private fun writeMem(address: Long, value: Long) {
        if (address !in 0..Int.MAX_VALUE) error("Invalid write address: $address")
        ram[address.toInt()] = value
    }

    private fun checkAddress(address: Long): Int = address.also { check(it in Int.MIN_VALUE..Int.MAX_VALUE) }.toInt()

    private val argModeMasks = listOf(100, 1000, 10000)
    private fun argMode(opCode: Long, argNo: Int) = ((opCode / argModeMasks[argNo]) % 10).toInt()
    private fun readNext(): Long = readMem((ip++).toLong())
    private fun readArg(mode: Int) = when(mode) {
        0 -> readMem(readNext())
        1 -> readNext()
        2 -> readMem(bp + readNext())
        else -> { println("DEBUG: invalid arg mode $mode at IP:$ip"); readNext() }
    }
    private fun writeArg(mode: Int, value: Long) = when(mode) {
        0 -> writeMem(readNext(), value)
        2 -> writeMem(bp + readNext(), value)
        else -> error("Invalid arg mode #mode at IP:$ip")
    }


    private fun advance(): CommandResult {
        output = null
        val ip_ = ip
        fun restoreIp() { ip = ip_ }
        val opCode = readNext()
        val mode0 = argMode(opCode, 0)
        val mode1 = argMode(opCode, 1)
        val mode2 = argMode(opCode, 2)

        when (val op = (opCode % 100).toInt()) {
            OpCodes.HALT -> return CommandResult.Halt
            OpCodes.ADD -> (readArg(mode0) + readArg(mode1)).let { writeArg(mode2, it) }
            OpCodes.MUL -> (readArg(mode0) * readArg(mode1)).let { writeArg(mode2, it) }
            OpCodes.INP -> {
                val input = inputs.removeFirstOrNull() ?: return CommandResult.WaitInput.also { restoreIp() }
                writeArg(mode0, input)
            }
            OpCodes.OUT -> output = readArg(mode0)
            OpCodes.JT -> { val v = readArg(mode0); val t = checkAddress(readArg(mode1)); if (v != 0L) ip = t }
            OpCodes.JF -> { val v = readArg(mode0); val t = checkAddress(readArg(mode1)); if (v == 0L) ip = t }
            OpCodes.LT -> (readArg(mode0) < readArg(mode1)).let { writeArg(mode2, if (it) 1 else 0) }
            OpCodes.EQ -> (readArg(mode0) == readArg(mode1)).let { writeArg(mode2, if (it) 1 else 0) }
            OpCodes.SBP -> bp = checkAddress(bp + readArg(mode0))
            else -> println("DEBUG: Unknown opcode $op")
        }
        return CommandResult.Continue
    }
}
