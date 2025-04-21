package dev.tuskyparadise

import dev.tuskyparadise.SCNumber.Companion.MAX_VALUE
import java.nio.ByteBuffer

enum class NumberCategory {
    NUMBER, REGISTER, INVALID
}

class SCNumber(
    var value: Int = 0,
    var category: NumberCategory = NumberCategory.NUMBER
) {

    /**
     * ## High-bytes and low-bytes
     *
     * Example: 32768 == `0x8000`
     * - `0x80` (first set) is the high byte
     * - `0x00` (second set) is the low byte
     *
     * ### Little-endian
     * low-byte is put in memory first
     * Ex: `0x8000` **BE** == `0x0080` _LE_
     */
    constructor(bytePair: ByteArray) : this() {
        assert(bytePair.size == 2)

        val intBytes = ByteArray(4) { 0 }
        intBytes[0] = bytePair[0]
        intBytes[1] = bytePair[1]
        intBytes.reverse() // LB HB -> HB LB
        value = ByteBuffer.wrap(intBytes).int
        categorize()
    }

    constructor(intValue: Int) : this() {
        value = intValue
        categorize()
    }

    private fun categorize() {
        category = when (value) {
            in MIN_NUM_VALUE..MAX_NUM_VALUE -> NumberCategory.NUMBER
            in MIN_REG_VALUE..MAX_REG_VALUE -> NumberCategory.REGISTER
            else -> NumberCategory.INVALID
        }
    }

    fun targetRegister(): Int {
        return value  % MODULO_BASE
    }

    companion object {
        const val MIN_NUM_VALUE = 0x0000
        const val MAX_NUM_VALUE = 0x7FFF

        const val MIN_REG_VALUE = 0x8000
        const val MAX_REG_VALUE = 0x8007

        const val MAX_VALUE = 0xFFFF

        const val MODULO_BASE = 0x8000
    }

    override fun toString(): String {
        return when (category) {
            NumberCategory.NUMBER -> "NUMBER(value=$value)"
            NumberCategory.REGISTER -> "REGISTER(target=${targetRegister()})"
            NumberCategory.INVALID -> "INVALID(value=$value)"
        }
    }
}

fun SCNumber.resolveValue(registers: Array<SCNumber>): Int {
    return when (this.category) {
        NumberCategory.NUMBER -> this.value
        NumberCategory.REGISTER -> registers[this.targetRegister()].value
        NumberCategory.INVALID -> {
            println("!!! Invalid number! Value: ${this.value} !!!")
            println("=============== HALTING EXECUTION ===============")
            return MAX_VALUE
        }
    }
}
