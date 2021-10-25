package visitor

import tokenizer.Bracket
import tokenizer.NumberToken
import tokenizer.Operation
import tokenizer.Token

object PrintVisitor: TokenVisitor {
    override fun visit(token: Bracket) = println(token)

    override fun visit(token: NumberToken) = println(token)

    override fun visit(token: Operation) = println(token)

    fun visit(tokens: List<Token>): String {
        val result =  tokens.joinToString(" ") { it.toString() }
        println(result)
        return result
    }
}