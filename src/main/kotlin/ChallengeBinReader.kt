package dev.tuskyparadise

class ChallengeBinReader {
    val sourceFilePath: String = "/challenge.bin"
    var contents: MutableList<Int> = mutableListOf()

    constructor() {
        val fileContents = this::class.java.getResourceAsStream(sourceFilePath)!!
        val bytePair = ByteArray(2)
        while (fileContents.available() > 0) {
            fileContents.read(bytePair)
            val intValue = IntValue(bytePair)
            contents.add(intValue.value)
        }
    }
}