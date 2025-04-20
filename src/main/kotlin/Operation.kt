package dev.tuskyparadise

class Operation(
    val opCode: OperationType,
    val args: Array<Int>
) {
    fun executeOperation(): Boolean {
        val shouldContinue = when (opCode) {
            OperationType.NOOP -> true      // do nothing, but continue
            OperationType.OUT -> {          // output arg1
                print(Char(args[0]))
                return true
            }
            OperationType.HALT -> false     // stop program
        }
        return shouldContinue
    }
}