package dev.tuskyparadise

/** Next operation:
 * - JT: 7 a b
 *   - if {a} is non-zero, jump to {b}
 */
fun main() {
    /**
     * # Main Loop
     * **TODO:** gradually split things into `fun`s, then `class`es
     */
    val processor = Processor()
    while (processor.programCounter < SCNumber.MAX_VALUE) {
        processor.processNextOperation()
        processor.executeCurrentOperation()
    }
}