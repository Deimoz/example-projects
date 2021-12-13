import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder
import java.text.ParseException

class LexicalAnalyzer(private val input: InputStream) {
    var curPos = 0
        private set
    var curChar = ' '
        private set
    var curToken = Tokens.EPS
        private set
    var lastWord = ""
        private set

    constructor(str: String) : this(str.byteInputStream())

    init {
        nextChar()
    }

    private fun isBlank(c: Char): Boolean {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t'
    }

    private fun nextChar() {
        curPos++
        try {
            curChar = input.read().toChar()
        } catch (e: IOException) {
            // change
            throw ParseException(e.message, curPos)
        }
    }

    fun nextToken() {
        while(isBlank(curChar)) {
            nextChar()
        }

        var isWord = false
        val curWord = buildString {
            if (curChar.isLetter()) {
                isWord = true
                append(curChar)
                nextChar()
            }

            while (curChar.isLetterOrDigit()) {
                append(curChar)
                nextChar()
            }
        }

        if (isWord) {
            curToken = when (curWord) {
                "var" -> Tokens.VAR
                "Array" -> Tokens.ARRAY
                else -> Tokens.NAME
            }
            lastWord = curWord
            return
        }

        curToken = when (curChar) {
            ':' -> Tokens.DOUBLE_COLON
            ';' -> Tokens.SEMICOLON
            ',' -> Tokens.COMMA
            '<' -> Tokens.OPEN
            '>' -> Tokens.CLOSE
            (-1).toChar() -> Tokens.END
            else -> throw ParseException("Illegal character", curPos)
        }

        if (curToken != Tokens.END) {
            nextChar()
        }
    }
}

fun main() {
    val l = LexicalAnalyzer("var x: Array<Int>;".byteInputStream())
    do {
        l.nextToken()
        val token = l.curToken
        println(token)
    } while (token != Tokens.END)
}