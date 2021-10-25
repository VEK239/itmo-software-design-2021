package visitor

import tokenizer.Bracket
import tokenizer.NumberToken
import tokenizer.Operation
import tokenizer.Token

interface TokenVisitor {
    fun visit(token: Bracket)
    fun visit(token: NumberToken)
    fun visit(token: Operation)
}
