package tokenizer

import java.lang.IllegalArgumentException

object Tokenizer {
    private val OPERATION_STATE = OperationState()
    private var currentState: State = OPERATION_STATE
    private var tokens = mutableListOf<Token>()

    fun tokenize(expression: String): List<Token> {
        tokens.clear()
        currentState = OPERATION_STATE
        expression.forEach {
            currentState.processChar(it)
        }
        currentState.processEndOfFile()
        return tokens
    }

    private abstract class State {
        abstract fun processChar(char: Char)
        open fun processEndOfFile() {}
    }

    private class OperationState: State() {
        override fun processChar(char: Char) {
            when(char) {
                '+' -> tokens.add(Plus)
                '-' -> tokens.add(Minus)
                '*' -> tokens.add(Mul)
                '/' -> tokens.add(Div)
                '(' -> tokens.add(LeftBracket)
                ')' -> tokens.add(RightBracket)
                in '0'..'9' -> {
                    currentState = NumberState()
                    currentState.processChar(char)
                }
                else -> {
                    if (!char.isWhitespace()) {
                        throw IllegalArgumentException("Unknown char: $char.")
                    }
                }
            }
        }
    }

    private class NumberState: State() {
        private var number = 0
        override fun processChar(char: Char) {
            when(char) {
                in '0'..'9' -> {
                    number = number * 10 + char.toString().toInt()
                }
                else -> {
                    tokens.add(NumberToken(number))
                    currentState = OPERATION_STATE
                    currentState.processChar(char)
                }
            }
        }

        override fun processEndOfFile() {
            tokens.add(NumberToken(number))
        }
    }
}
