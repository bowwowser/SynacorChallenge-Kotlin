package dev.tuskyparadise.operations

class Operation(
    val opCode: OperationType,
    val args: IntArray
) {
    fun executeOperation(programCounter: Int): Int {
        val newProgramCounter = when (opCode) {
            OperationType.NOOP -> programCounter + 1      // do nothing, but continue
            OperationType.OUT -> {          // output arg1
                print(Char(args[0]))
                return programCounter + 1
            }
            OperationType.JMP -> {
                return args[0]
            }
            OperationType.HALT -> -1     // stop program
        }
        return newProgramCounter
    }
}