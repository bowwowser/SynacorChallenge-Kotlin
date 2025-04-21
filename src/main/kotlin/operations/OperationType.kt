package dev.tuskyparadise.operations

enum class OperationType(
    val opCode: Int,
    val numArgs: Int = 0,
) {
    HALT(0),
    SET(1, 2),
    PUSH(2, 1),
    POP(3, 1),
    EQ(4, 3),
    GT(5, 3),
    JMP(6, 1),
    JT(7, 2),
    JF(8, 2),
    ADD(9, 3),
    AND(12, 3),
    OR(13, 3),
    NOT(14, 2),
    CALL(17, 1),
    OUT(19, 1),
    NOOP(21);

    companion object {
        fun fromInt(opCode: Int) = entries.first { it.opCode == opCode }
    }
}