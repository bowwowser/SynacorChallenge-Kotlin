package dev.tuskyparadise

import java.util.Stack

/** Start with operations:
 * - 0:  halt
 * - 19: out
 * - 21: noop
 */
fun main() {

    /**
     * ## Storage 1 ##
     * Memory with 15-bit **address** space
     * stores: 16-bit **value**s
     */
    val storage1: Int
    val storage2: Int
    val storage3: Int

    /**
     * ## Storage 2
     * Eight registers
     */
    val registers = Array(8) { 0 }

    /**
     * ## Storage 3
     * Unbounded stack (holds 16-bit values)
     */
    val stack = Stack<Int>()

    /**
     * # Main Loop
     * **TODO:** gradually split things into `fun`s, then `class`es
     */
    val message = "Hello World!"
    val messageCodes = message.map { "19,${it.code}" }.joinToString(separator = ",")
    val programString = "${messageCodes},21,0"
    val program = programString.split(",").map { it.toInt() }

    var argCount = 0

    val operations = mutableListOf<Operation>()
    for (command in program) {
        // Parse operation
        if (argCount > 0) {
            val lastOp = operations.last()
            println("Parsing arg; value = $command")
            lastOp.args[lastOp.opCode.numArgs - argCount] = command
            argCount = argCount.dec()
        } else {
            val opType = OperationType.fromInt(command)
            println("Parsing command; command = $opType")
            println("Num of args? ${opType.numArgs}")
            operations.add(
                Operation(
                    opType,
                    Array(opType.numArgs) { 0 }
                )
            )
            argCount = opType.numArgs
        }
    }

    for (operation in operations) {
        val shouldContinue = operation.executeOperation()
        if (!shouldContinue) {
            break
        }
    }
}