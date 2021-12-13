import java.text.ParseException

data class Tree(val node: String, val isTerm: Boolean, val children: List<Tree>) {
    constructor(node: String, isTerm: Boolean, vararg childNodes: Tree) : this(node, isTerm, childNodes.asList())

    override fun toString(): String {
        return buildString {
            if (node == "eps") {
                return@buildString
            }
            if (isTerm) {
                append(node)
            }
            if (node in setOf("var", ":", ",")) {
                append(" ")
            }
            children.forEach { child ->
                append(child.toString())
            }
        }
    }
}

class Parser(var analyzer: LexicalAnalyzer) {
    init {
        analyzer.nextToken()
    }

    fun parse(): Tree {
        return S()
    }

    private fun constValues(token: Tokens): Tree {
        if (analyzer.curToken == Tokens.END && token == Tokens.SEMICOLON) {
            return Tree("eps", true)
        }
        if (analyzer.curToken != token && token != Tokens.EPS) {
            throw ParseException("Wrong token", 0)
        }
        val lastWord = analyzer.lastWord
        if (token != Tokens.EPS) {
            analyzer.nextToken()
        }
        return Tree(
            when (token) {
                Tokens.DOUBLE_COLON -> ":"
                Tokens.COMMA -> ","
                Tokens.ARRAY -> "Array"
                Tokens.SEMICOLON -> ";"
                Tokens.VAR -> "var"
                Tokens.OPEN -> "<"
                Tokens.CLOSE -> ">"
                Tokens.EPS -> "eps"
                Tokens.NAME -> lastWord
                else -> token.toString()
            },
            true
        )
    }

    private fun S(): Tree = when (analyzer.curToken) {
        Tokens.VAR -> Tree(
            "S",
            false,
            V(),
            N(),
            constValues(Tokens.DOUBLE_COLON),
            A(),
            constValues(Tokens.OPEN),
            T(),
            constValues(Tokens.CLOSE),
            C()
        )
        else -> throw ParseException("Wrong token", 0)
    }

    private fun V(): Tree = constValues(Tokens.VAR)

    private fun N(): Tree = constValues(Tokens.NAME)

    private fun A(): Tree = constValues(Tokens.ARRAY)

    private fun C(): Tree = constValues(Tokens.SEMICOLON)

    private fun T(): Tree = Tree("T", false, N(), T1())

    private fun T1(): Tree = when (analyzer.curToken) {
        Tokens.OPEN -> Tree("T1", false, constValues(Tokens.OPEN), T2(), constValues(Tokens.CLOSE))
        Tokens.CLOSE, Tokens.COMMA -> Tree("T1", false, constValues(Tokens.EPS))
        else -> throw ParseException("Wrong token", 0)
    }

    private fun T2(): Tree = when (analyzer.curToken) {
        Tokens.NAME -> Tree("T2", false, T(), T3())
        else -> throw ParseException("Wrong token", 0)
    }

    private fun T3(): Tree = when (analyzer.curToken) {
        Tokens.COMMA -> Tree("T3", false, constValues(Tokens.COMMA), T2())
        Tokens.CLOSE -> Tree("T3", false, constValues(Tokens.EPS))
        else -> throw ParseException("Wrong token", 0)
    }
}

fun main() {
    val p = Parser(LexicalAnalyzer("var a: Array<Int>"))
    println(p.parse())
    val p1 = Parser(LexicalAnalyzer("var a: Array<Map<String, Pair<Int, Int>>>;"))
    println(p1.parse())
    val p2 = Parser(LexicalAnalyzer("var a: Array<Int, Int>"))
    println(p2.parse())
}