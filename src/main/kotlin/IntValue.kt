package dev.tuskyparadise

import java.nio.ByteBuffer

class IntValue(
    var value: Int = 0
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
        bytePair.reverse() // LB HB -> HB LB
        value = ByteBuffer.wrap(bytePair).short.toInt()
    }
}
