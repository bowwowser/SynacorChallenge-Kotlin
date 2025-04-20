package dev.tuskyparadise

enum class OperationType(
    val opCode: Int,
    val numArgs: Int = 0,
) {
    HALT(0),
    OUT(19, 1),
    NOOP(21);

    companion object {
        fun fromInt(opCode: Int) = entries.first { it.opCode == opCode }
    }
}