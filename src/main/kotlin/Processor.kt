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

    fun executeOperation(operation: Operation) {
        when (operation.opCode) {
            OperationType.NOOP -> return
            OperationType.SET -> {
                val registerTarget = operation.args[0].targetRegister()
                val newRegisterValue = operation.args[1]
                registers[registerTarget] = SCNumber(newRegisterValue.resolveValue(registers))
            }
            OperationType.PUSH -> {
                val newValue = operation.args[0].resolveValue(registers)
                stack.push(newValue)
            }
            OperationType.POP -> {
                val registerTarget = operation.args[0].targetRegister()
                registers[registerTarget] = SCNumber(stack.pop())
            }
            OperationType.OUT -> {
                val charCode = operation.args[0].resolveValue(registers)
                print(Char(charCode))
            }
            OperationType.EQ -> {
                val registerTarget = operation.args[0].targetRegister()
                val eq1 = operation.args[1].resolveValue(registers)
                val eq2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber(if (eq1 == eq2) 1 else 0)
            }
            OperationType.GT -> {
                val registerTarget = operation.args[0].targetRegister()
                val op1 = operation.args[1].resolveValue(registers)
                val op2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber(if (op1 > op2) 1 else 0)
            }
            OperationType.JMP -> {
                val target = operation.args[0].resolveValue(registers)
                programCounter = target
            }
            OperationType.JT -> {
                val condition = operation.args[0].resolveValue(registers)
                val target = operation.args[1].resolveValue(registers)
                if (condition != 0) {
                    programCounter = target
                }
            }
            OperationType.JF -> {
                val condition = operation.args[0].resolveValue(registers)
                val target = operation.args[1].resolveValue(registers)
                if (condition == 0) {
                    programCounter = target
                }
            }
            OperationType.ADD -> {
                val registerTarget = operation.args[0].targetRegister()
                val add1 = operation.args[1].resolveValue(registers)
                val add2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber((add1 + add2) % SCNumber.MODULO_BASE)
                return
            }
            OperationType.MULT -> {
                val registerTarget = operation.args[0].targetRegister()
                val mult1 = operation.args[1].resolveValue(registers)
                val mult2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber((mult1 * mult2) % SCNumber.MODULO_BASE)
                return
            }
            OperationType.MOD -> {
                val registerTarget = operation.args[0].targetRegister()
                val mod1 = operation.args[1].resolveValue(registers)
                val mod2 = operation.args[2].resolveValue(registers)
                // TODO is this redundant? test later
                registers[registerTarget] = SCNumber((mod1 % mod2) % SCNumber.MODULO_BASE)
                return
            }
            OperationType.AND -> {
                val registerTarget = operation.args[0].targetRegister()
                val and1 = operation.args[1].resolveValue(registers)
                val and2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber(and1 and and2)
            }
            OperationType.OR -> {
                val registerTarget = operation.args[0].targetRegister()
                val and1 = operation.args[1].resolveValue(registers)
                val and2 = operation.args[2].resolveValue(registers)
                registers[registerTarget] = SCNumber(and1 or and2)
            }
            OperationType.NOT -> {
                val registerTarget = operation.args[0].targetRegister()
                val not1 = operation.args[1].resolveValue(registers)
                registers[registerTarget] = SCNumber(not1).bitwiseInverse()
            }
            OperationType.RMEM -> {
                val registerTarget = operation.args[0].targetRegister()
                val memoryLocation = operation.args[1].resolveValue(registers)
                registers[registerTarget] = SCNumber(memory[memoryLocation])
            }
            OperationType.WMEM -> {
                val memoryLocation = operation.args[0].resolveValue(registers)
                val targetValue = operation.args[1].resolveValue(registers)
                memory[memoryLocation] = targetValue
            }
            OperationType.CALL -> {
                stack.push(programCounter)
                val jumpTarget = operation.args[0].resolveValue(registers)
                programCounter = jumpTarget
            }
            OperationType.HALT -> {
                programCounter = PC_EXIT
            }
        }
    }

    fun executeCurrentOperation() {
        executeOperation(currentOperation)
    }

    companion object {
        const val PC_EXIT = SCNumber.MAX_VALUE
    }
}