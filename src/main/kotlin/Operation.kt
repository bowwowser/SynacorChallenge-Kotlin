package dev.tuskyparadise

class Operation(
    val opCode: OperationType,
    val args: Array<Int>
) {
    fun executeOperation(): Boolean {
        val shouldContinue = when (opCode) {
            OperationType.HALT -> true
            OperationType.OUT -> {
                print(Char(args[0]))
                return true
            }
            OperationType.NOOP -> false
        }
        return shouldContinue
    }
}