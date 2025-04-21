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
            currentOperation = Operation(OperationType.HALT, arrayOf())
        }
    }

    fun executeCurrentOperation() {
        when (currentOperation.opCode) {
            OperationType.NOOP -> return
            OperationType.SET -> {
                val registerTarget = currentOperation.args[0].targetRegister()
                val newRegisterValue = currentOperation.args[1]
                registers[registerTarget] = SCNumber(newRegisterValue.resolveValue(registers))
            }
            OperationType.PUSH -> {
                val newValue = currentOperation.args[0].resolveValue(registers)
                stack.push(newValue)
            }
            OperationType.POP -> {
                val registerTarget = currentOperation.args[0].targetRegister()
                registers[registerTarget] = SCNumber(stack.pop())
            }
            OperationType.OUT -> {
                val charCode = currentOperation.args[0].resolveValue(registers)
                print(Char(charCode))
            }
            OperationType.EQ -> {
                val registerTarget = currentOperation.args[0].targetRegister()
                val eq1 = currentOperation.args[1].resolveValue(registers)
                val eq2 = currentOperation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber(if (eq1 == eq2) 1 else 0)
            }
            OperationType.JMP -> {
                val target = currentOperation.args[0].resolveValue(registers)
                programCounter = target
            }
            OperationType.JT -> {
                val condition = currentOperation.args[0].resolveValue(registers)
                val target = currentOperation.args[1].resolveValue(registers)
                if (condition != 0) {
                    programCounter = target
                }
            }
            OperationType.JF -> {
                val condition = currentOperation.args[0].resolveValue(registers)
                val target = currentOperation.args[1].resolveValue(registers)
                if (condition == 0) {
                    programCounter = target
                }
            }
            OperationType.ADD -> {
                val registerTarget = currentOperation.args[0].resolveValue(registers)
                val add1 = currentOperation.args[1].resolveValue(registers)
                val add2 = currentOperation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber((add1 + add2) % SCNumber.MODULO_BASE)
                return
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