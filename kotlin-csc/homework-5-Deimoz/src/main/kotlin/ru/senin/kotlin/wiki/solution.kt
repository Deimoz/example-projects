package ru.senin.kotlin.wiki

import org.apache.commons.compress.compressors.CompressorException
import org.xml.sax.helpers.DefaultHandler
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.SAXException
import java.io.*

import javax.xml.parsers.ParserConfigurationException

import org.apache.commons.compress.compressors.CompressorStreamFactory

import java.io.BufferedInputStream

import java.io.FileInputStream
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference

import java.util.concurrent.TimeUnit

const val AMOUNT_OF_WORDS = 300
const val MINUTES_FOR_SHUTDOWN = 30L

fun solve(parameters: Parameters) {
    val stats = AtomicReference(Statistics())
    val filesProcessed = AtomicInteger(0)
    val queue = ConcurrentLinkedQueue<InputStream>()
    val workers = Executors.newFixedThreadPool(parameters.threads)

    repeat(parameters.inputs.size) { i ->
        try {
            queue.add(unzip(parameters.inputs[i]))
        } catch (e: CompressorException) {
            println("Invalid archive: " + e.message)
            return
        }
    }

    val listener = Runnable {
        while (filesProcessed.get() < parameters.inputs.size) {
            val currFile = queue.poll() ?: continue
            filesProcessed.incrementAndGet()
            parse(stats, currFile)
        }
    }

    repeat(parameters.threads) {
        workers.execute(listener)
    }

    workers.shutdown()
    try {
        if (!workers.awaitTermination(MINUTES_FOR_SHUTDOWN, TimeUnit.MINUTES)) {
            workers.shutdownNow()
        }
    } catch (e: InterruptedException) {
        workers.shutdownNow()
        println("Couldn't finish process properly, given time is out: " + e.message)
    }

    printToFile(File(parameters.output), stats.get())
}

fun unzip(file: File): InputStream {
    val fin = FileInputStream(file)
    val bis = BufferedInputStream(fin)
    return CompressorStreamFactory()
        .createCompressorInputStream(CompressorStreamFactory.BZIP2, bis)
}

fun parse(stats: AtomicReference<Statistics>, currFile: InputStream) {
    try {
        val parserFactory = SAXParserFactory.newInstance()
        val saxParser = parserFactory.newSAXParser()

        val handler = object : DefaultHandler() {
            val MEDIA = "mediawiki"
            val REVISION = "revision"
            val PAGE = "page"
            val TEXT = "text"
            val TITLE = "title"
            val TIME = "timestamp"
            val BYTES = "bytes"

            var currentValue = StringBuilder()
            var currPage = PageInfo()
            var currentElement = false

            // to prevent nested tags
            var inMedia = 0
            var inPage = 0
            var inRevision = 0
            var depth = 0

            private fun inCorrectPage(): Boolean {
                return inMedia == 1 && inPage == 1 && depth == 2
            }

            private fun inCorrectRevision(): Boolean {
                return inMedia == 1 && inPage == 1 && inRevision == 1 && depth == 3
            }

            private fun inCorrectTitle(): Boolean {
                return inMedia == 1 && inPage == 1 && depth == 3
            }

            private fun inCorrectTextOrTimestamp(): Boolean {
                return inMedia == 1 && inPage == 1 && inRevision == 1 && depth == 4
            }

            override fun startElement(
                uri: String,
                localName: String,
                qName: String,
                attributes: org.xml.sax.Attributes
            ) {
                currentElement = true
                currentValue = StringBuilder()
                when (qName) {
                    MEDIA -> inMedia++
                    PAGE -> inPage++
                    REVISION -> inRevision++
                    TEXT -> {
                        if (attributes.length > 0 && inCorrectRevision()) {
                            currPage.currPageBytes = attributes.getValue(BYTES).toInt()
                        }
                    }
                }
                depth++
            }

            override fun endElement(uri: String, localName: String, qName: String) {
                currentElement = false
                val value = currentValue.toString()
                when (qName) {
                    PAGE -> {
                        if (inCorrectPage() && currPage.correctPage()) {
                            stats.get().makeStatsFromPage(currPage)
                        }
                        inPage--
                        currPage = PageInfo()
                    }
                    REVISION -> inRevision--
                    MEDIA -> inMedia--
                    TITLE -> {
                        if (inCorrectTitle()) {
                            currPage.currPageTitle = value
                        }
                    }
                    TEXT -> {
                        if (inCorrectTextOrTimestamp()) {
                            currPage.currPageText = value
                        }
                    }
                    TIME -> {
                        if (inCorrectTextOrTimestamp()) {
                            currPage.currPageYear = value
                        }
                    }
                }
                depth--
            }

            override fun characters(ch: CharArray, start: Int, length: Int) {
                if (currentElement) {
                    currentValue.append(String(ch, start, length))
                }
            }
        }

        saxParser.parse(currFile, handler)
    } catch (e: IOException) {
        println("Error while reading text: " + e.message)
    } catch (e: ParserConfigurationException) {
        println("Error with parser configuration: " + e.message)
    } catch (e: SAXException) {
        println("Error while parsing: " + e.message)
    }
}

fun printToFile(file: File, stats: Statistics) {
    file.bufferedWriter().use { out ->
        printStat(
            "Топ-300 слов в заголовках статей:",
            stats.analyzedTitleStats(),
            true,
            out
        )
        out.newLine()

        printStat(
            "Топ-300 слов в статьях:",
            stats.analyzedTextStats(),
            true,
            out
        )
        out.newLine()

        printStat(
            "Распределение статей по размеру:",
            stats.analyzedSizeStats(),
            false,
            out
        )
        out.newLine()

        printStat(
            "Распределение статей по времени:",
            stats.analyzedYearStats(),
            false,
            out
        )
    }
}

fun <E, T> printStat(
    message: String,
    list: List<Pair<E, T>>,
    isReversed: Boolean,
    out: BufferedWriter
) {
    out.write(message)
    out.newLine()
    list.forEach { (key, value) ->
        if (isReversed) {
            out.write("$value $key")
        } else {
            out.write("$key $value")
        }
        out.newLine()
    }
}

class Statistics {
    private val textStat = ConcurrentHashMap<String, Int>()
    private val titleStat = ConcurrentHashMap<String, Int>()
    private val yearStat = ConcurrentHashMap<Int, Int>()
    private val sizeStat = ConcurrentHashMap<Int, Int>()

    fun analyzedTextStats(): List<Pair<String, Int>> {
        return analyzedWordsStats(textStat)
    }

    fun analyzedTitleStats(): List<Pair<String, Int>> {
        return analyzedWordsStats(titleStat)
    }

    fun analyzedYearStats(): List<Pair<Int, Int>> {
        val res = yearStat.toSortedMap()
        if (res.size > 0) {
            (res.firstKey()..res.lastKey()).forEach { year ->
                res.putIfAbsent(year, 0)
            }
        }
        return res.toList()
    }

    fun analyzedSizeStats(): List<Pair<Int, Int>> {
        val res = sizeStat.toSortedMap()
        var slice = 0
        for ((_, number) in res) {
            if (number == 0) {
                slice++
            } else {
                break
            }
        }
        return res.toList().slice(slice until res.size)
    }

    fun makeStatsFromPage(page: PageInfo) {
        makeTextStat(page.currPageText)
        makeTitleStat(page.currPageTitle)
        makeYearStat(page.currPageYear)
        makeSizeStat(page.currPageBytes)
    }

    private fun makeTextStat(text: String) {
        makeWordStat(text, textStat)
    }

    private fun makeTitleStat(text: String) {
        makeWordStat(text, titleStat)
    }

    private fun makeYearStat(time: String) {
        val year = time.take(4).toInt()
        synchronized(yearStat) {
            yearStat[year] = yearStat.getOrDefault(year, 0) + 1
        }
    }

    private fun makeSizeStat(bytes: Int) {
        var log = 0
        var currBytes = bytes
        while (currBytes / 10 > 0) {
            synchronized(sizeStat) {
                sizeStat[log] = sizeStat.getOrDefault(log, 0)
            }
            currBytes /= 10
            log++
        }
        synchronized(sizeStat) {
            sizeStat[log] = sizeStat.getOrDefault(log, 0) + 1
        }
    }

    private fun analyzedWordsStats(map: Map<String, Int>): List<Pair<String, Int>> {
        return map
            .toList()
            .sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })
            .take(AMOUNT_OF_WORDS)
    }

    private fun makeWordStat(text: String, map: MutableMap<String, Int>) {
        Regex("[а-яА-Я]{3,}")
            .findAll(text.lowercase(Locale.getDefault()))
            .forEach { word ->
                val str = word.value
                synchronized(map) {
                    map[str] = map.getOrDefault(str, 0) + 1
                }
            }
    }
}

class PageInfo {
    var currPageTitle = ""
        set(value) {
            if (checkMultiTag(hasTitle)) return
            hasTitle = true
            field = value
        }

    var currPageText = ""
        set(value) {
            if (checkMultiTag(hasText)) return
            hasText = true
            field = value
        }

    var currPageYear = ""
        set(value) {
            if (checkMultiTag(hasTime)) return
            hasTime = true
            field = value
        }

    var currPageBytes = -1

    private var hasTitle = false
    private var hasText = false
    private var hasTime = false
    private var hasMultipleTags = false

    fun correctPage(): Boolean {
        return !hasMultipleTags && hasText && hasTime && hasTitle && currPageBytes != -1
    }

    private fun checkMultiTag(check: Boolean): Boolean {
        if (check) {
            hasMultipleTags = true
        }
        return hasMultipleTags
    }
}