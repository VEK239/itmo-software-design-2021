package visitor

import tokenizer.*

object EvaluationVisitor : TokenVisitor {
    private val stack = mutableListOf<Int>()

    override fun visit(token: Bracket) {
        throw IllegalArgumentException("Incorrect reverse polish notation expression in token $token")
    }

    override fun visit(token: NumberToken) {
        stack.add(token.value)
    }

    override fun visit(token: Operation) {
        if (stack.size < 2) {
            throw IllegalArgumentException("Incorrect reverse polish notation expression $token")
        }
        val a = stack.removeLast()
        val b = stack.removeLast()
        when (token) {
            is Plus -> {
                stack.add(a + b)
            }
            is Minus -> {
                stack.add(b - a)
            }
            is Mul -> {
                stack.add(a * b)
            }
            is Div -> {
                stack.add(b / a)
            }
        }
    }


    fun visit(tokens: List<Token>): Int {
        stack.clear()
        tokens.forEach {
            it.accept(this)
        }
        if (stack.size != 1) {
            throw IllegalArgumentException("Incorrect reverse polish notation expression")
        }
        return stack[0]
    }
}