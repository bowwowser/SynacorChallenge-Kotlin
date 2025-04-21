package dev.tuskyparadise.operations

enum class OperationType(
    val opCode: Int,
    val numArgs: Int = 0,
) {
    HALT(0),
    JMP(6, 1),
    JT(7, 2),
    JF(8, 2),
    ADD(9, 3),
    OUT(19, 1),
    NOOP(21);

    companion object {
        fun fromInt(opCode: Int) = entries.first { it.opCode == opCode }
    }
}