package dev.tuskyparadise.readers

class AdventureCommandReader {
    val sourceFilePath: String = "/adventureCommands.txt"
    var contents: MutableList<String> = mutableListOf()

    constructor() {
        val commandFileReader = this::class.java.getResourceAsStream(sourceFilePath)!!.bufferedReader()
        commandFileReader.lines().forEach { contents.add(it) }
    }
}