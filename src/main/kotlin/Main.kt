package dev.tuskyparadise

import dev.tuskyparadise.operations.Operation
import dev.tuskyparadise.operations.OperationType
import java.util.Stack

/** Next operation:
 * - 6: JMP a
 *   - jump to {a}
 */
fun main() {

    /**
     * ## Storage 1 ##
     * Memory with 15-bit **address** space
     * stores: 16-bit **value**s
     */
    val memory = IntArray(32768)

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
    var programCounter = 0

    // Load binary from challenge.bin into memory
    ChallengeBinReader().contents
        .withIndex()
        .forEach {
            memory[it.index] = it.value
        }

    var argCount = 0

    var currentOperation = Operation(OperationType.NOOP, intArrayOf())
    while (programCounter >= 0) {
        // Parse Operations
        val command = memory[programCounter]
        if (argCount > 0) {
//            println("Parsing arg; value = $command")
            currentOperation.args[currentOperation.opCode.numArgs - argCount] = command
            argCount--
        } else {
            try {
                val opType = OperationType.fromInt(command)
//                println("Parsing command; command = $opType")
//                println("Num of args? ${opType.numArgs}")
                currentOperation = Operation(opType, IntArray(opType.numArgs))
                argCount = opType.numArgs
            } catch (_: NoSuchElementException) {
                println("!!! Could not parse command with opcode [${command}] !!!")
                println("=============== HALTING PROCESSING ===============")
                break
            }
        }

        if (argCount == 0) {
            val newProgramCounter = currentOperation.executeOperation(programCounter)
            programCounter = newProgramCounter
        } else {
            programCounter++
        }
    }
}