package expression

import tokenizer.Tokenizer
import visitor.EvaluationVisitor
import visitor.PrintVisitor
import visitor.ParserVisitor

class Expression(private val expression: String) {
    private val tokens = Tokenizer.tokenize(expression)

    private val polishNotationTokens = ParserVisitor.visit(tokens)

    fun printTokens(): String {
        return PrintVisitor.visit(tokens)
    }

    fun printReversePolishNotation(): String {
        return PrintVisitor.visit(polishNotationTokens)
    }

    fun evaluate(): Int {
        return EvaluationVisitor.visit(polishNotationTokens)
    }

    fun getAllInformation() {
        printTokens()
        printReversePolishNotation()
        println("$expression = ${evaluate()}")
    }
}