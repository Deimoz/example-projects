package org.csc.kotlin2021.mastermind.recordtable

import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException

const val JSON_FILE: String = "record-table.json"

typealias Table = MutableMap<Int, MutableMap<Boolean, MutableList<Int>>>

/**
 * Record table holds only 3 best scores of each category
 */
class TableHolder {
    private var table: RecordTable

    init {
        val gson = Gson()
        try {
            table = gson.fromJson(File(JSON_FILE).readText(), RecordTable::class.java)
        } catch (e: FileNotFoundException) {
            table = RecordTable()
            updateFile()
        }
    }

    fun addResult(length: Int, canRepeat: Boolean, tries: Int) {
        val map = table.recordTable
        map.putIfAbsent(length, mutableMapOf())
        map.getValue(length).putIfAbsent(canRepeat, mutableListOf())
        map.getValue(length).putIfAbsent(canRepeat, mutableListOf())
        val finalList = map.getValue(length).getValue(canRepeat)
        finalList.add(tries)
        if (finalList.size > 3) {
            finalList.sort()
            finalList.removeLast()
        }
        updateFile()
    }

    fun standardTable() {
        val map = table.recordTable
        if (!map.containsKey(4)) {
            println("No records in standard mode")
            return
        }
        printTable(4, false, map[4]?.get(false))
        printTable(4, true, map[4]?.get(true))
    }

    fun findTable(length: Int, canRepeat: Boolean) {
        printTable(length, canRepeat, table.recordTable[length]?.get(canRepeat))
    }

    private fun updateFile() {
        File(JSON_FILE).writeText(Gson().toJson(table).toString())
    }

    private fun printTable(length: Int, canRepeat: Boolean, list: MutableList<Int>?) {
        println("Personal records: length = $length, repeat = $canRepeat")
        if (list == null) {
            println("No records was set")
            return
        }
        list.forEach { item ->
            println("Guesses: $item")
        }
    }

    private data class RecordTable(val recordTable: Table = mutableMapOf())
}
