package dev.tuskyparadise.readers

import dev.tuskyparadise.SCNumber

class ChallengeBinReader {
    val sourceFilePath: String = "/challenge.bin"
    var contents: MutableList<Int> = mutableListOf()

    constructor() {
        val fileContents = this::class.java.getResourceAsStream(sourceFilePath)!!
        val bytePair = ByteArray(2)
        while (fileContents.available() > 0) {
            fileContents.read(bytePair)
            val intValue = SCNumber(bytePair)
            contents.add(intValue.value)
        }
    }
}