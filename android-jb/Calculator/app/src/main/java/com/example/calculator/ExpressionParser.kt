package com.example.calculator

import java.lang.StringBuilder

class ExpressionParser(_expression: String) {
    lateinit var expr: String
    var currPos: Int = 0
    var currentState = CurrentState.NORMALEXPRESSION
    init {
        expr = _expression
    }

    private fun errorName(): String {
        when (currentState) {
            CurrentState.SYNTAXERROR -> {
                return "syntax error"
            }
            CurrentState.DIVIDEBYZERO -> {
                return "divide by zero"
            }
            else -> {
                return "normal expression"
            }
        }

    }

    private fun doOperation(op: Char, num1: Float, num2: Float): Float {
        when (op) {
            '+' -> {
                return num1 + num2
            }
            '-' -> {
                return num1 - num2
            }
            '/' -> {
                if (num2 == 0F) {
                    currentState = CurrentState.DIVIDEBYZERO
                    return 0F
                }
                return num1 / num2
            }
            '*' -> {
                return num1 * num2
            } else -> {
                currentState = CurrentState.SYNTAXERROR
                return 0F
            }
        }
    }

    fun parse() : String {
        var res: Float = parseNumber()
        if (currentState != CurrentState.NORMALEXPRESSION) {
            return errorName()
        }
        while (currPos < expr.length) {
            val op: Char = expr[currPos++]
            var num1: Float = parseNumber()
            if (currentState != CurrentState.NORMALEXPRESSION) {
                return errorName()
            }
            while (currPos < expr.length && (expr[currPos] == '*' || expr[currPos] == '/')) {
                val op1: Char = expr[currPos++]
                val num2: Float = parseNumber()
                if (currentState != CurrentState.NORMALEXPRESSION) {
                    return errorName()
                }
                num1 = doOperation(op1, num1, num2)
                if (currentState != CurrentState.NORMALEXPRESSION) {
                    return errorName()
                }
            }
            res = doOperation(op, res, num1)
            if (currentState != CurrentState.NORMALEXPRESSION) {
                return errorName()
            }
        }
        return res.toString()
    }

    private fun parseNumber() : Float {
        val number = StringBuilder()

        if (expr[currPos] == '-') {
            number.append(expr[currPos++])
        }
        if (expr[currPos].isDigit()) {
            number.append(expr[currPos++])
            if (expr[currPos - 1] == '0' && currPos < expr.length && expr[currPos].isDigit()) {
                currentState = CurrentState.SYNTAXERROR
                return 0F
            }
        } else {
            currentState = CurrentState.SYNTAXERROR
            return 0F
        }
        while (currPos < expr.length && expr[currPos].isDigit()) {
            number.append(expr[currPos++])
        }
        if (currPos < expr.length && expr[currPos] == '.') {
            number.append('.')
            currPos++
            if (currPos < expr.length && !expr[currPos].isDigit()) {
                currentState = CurrentState.SYNTAXERROR
                return 0F
            }
        }
        while (currPos < expr.length && expr[currPos].isDigit()) {
            number.append(expr[currPos++])
        }
        if (currPos < expr.length && expr[currPos] == '.') {
            currentState = CurrentState.SYNTAXERROR
            return 0F
        }
        val res: String = number.toString()
        if (res.isEmpty() && res.equals("-")) {
            return 0F
        }
        return res.toFloat()
    }
}