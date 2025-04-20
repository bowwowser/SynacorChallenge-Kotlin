package dev.tuskyparadise

import dev.tuskyparadise.operations.Operation
import dev.tuskyparadise.operations.OperationType
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
    // Get binary from challenge.bin
    val challengeProgram = ChallengeBinReader().contents

    // Parse Operations
    var argCount = 0
    val operations = mutableListOf<Operation>()
    for (command in challengeProgram) {
        if (argCount > 0) {
            val lastOp = operations.last()
            println("Parsing arg; value = $command")
            lastOp.args[lastOp.opCode.numArgs - argCount] = command
            argCount = argCount.dec()
        } else {
            try {
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
            } catch (_: NoSuchElementException) {
                println("!!! Could not parse command with opcode [${command}] !!!")
                println("=============== HALTING PROCESSING ===============")
                break
            }
        }
    }

    // Execute operations
    println("++++++++++++++++++ PROGRAM START +++++++++++++++++")
    for (operation in operations) {
        val shouldContinue = operation.executeOperation()
        if (!shouldContinue) {
            break
        }
    }
}