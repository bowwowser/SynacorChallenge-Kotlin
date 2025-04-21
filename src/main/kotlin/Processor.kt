package dev.tuskyparadise

import dev.tuskyparadise.operations.Operation
import dev.tuskyparadise.operations.OperationType
import java.util.Stack

class Processor {
    /**
     * ## Storage 1 ##
     * Memory with 15-bit **address** space
     * stores: 16-bit **value**s
     */
    val memory = IntArray(SCNumber.MAX_NUM_VALUE) // == 2^15

    /**
     * ## Storage 2
     * Eight registers
     */
    val registers = Array(8) { SCNumber() }

    /**
     * ## Storage 3
     * Unbounded stack (holds 16-bit values)
     */
    val stack = Stack<Int>()

    var programCounter: Int = 0

    constructor() {
        ChallengeBinReader().contents
            .withIndex()
            .forEach {
                memory[it.index] = it.value // Load binary from challenge.bin into memory
            }
    }

    var currentOperation = Operation(OperationType.NOOP, arrayOf())

    fun processNextOperation() {
        try {
            val opType = OperationType.fromInt(memory[programCounter])
            programCounter++
            val opArgs = Array(opType.numArgs) { SCNumber() }
            var argCount = opType.numArgs
            while (argCount > 0) {
                opArgs[opType.numArgs - argCount] = SCNumber(memory[programCounter])
                argCount--
                programCounter++
            }
            currentOperation = Operation(opType, opArgs)
        } catch (_: NoSuchElementException) {
            println("!!! Could not parse command with opcode [${memory[programCounter]}] !!!")
            println("=============== HALTING PROCESSING ===============")
            programCounter = PC_EXIT
        }
    }

    fun executeCurrentOperation() {
        when (currentOperation.opCode) {
            OperationType.NOOP -> return
            OperationType.OUT -> {
                val charCode = currentOperation.args[0]
                print(Char(charCode.resolveValue(registers)))
            }
            OperationType.JMP -> {
                val target = currentOperation.args[0]
                programCounter = target.resolveValue(registers)
            }
            OperationType.JT -> {
                val condition = currentOperation.args[0]
                val target = currentOperation.args[1]
                if (condition.resolveValue(registers) != 0) {
                    programCounter = target.resolveValue(registers)
                }
            }
            OperationType.JF -> {
                val condition = currentOperation.args[0]
                val target = currentOperation.args[1]
                if (condition.resolveValue(registers) == 0) {
                    programCounter = target.resolveValue(registers)
                }
            }
            OperationType.HALT -> {
                programCounter = PC_EXIT
                return
            }
        }
    }

    companion object {
        const val PC_EXIT = SCNumber.MAX_VALUE
    }
}