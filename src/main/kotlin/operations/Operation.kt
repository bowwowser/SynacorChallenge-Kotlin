package dev.tuskyparadise.operations

import dev.tuskyparadise.SCNumber

data class Operation(
    val opCode: OperationType,
    val args: Array<SCNumber>
) {
}