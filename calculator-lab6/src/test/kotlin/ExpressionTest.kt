import expression.Expression
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ExpressionTest {

    @Test
    fun testExpressionWithPlus() {
        val expression = Expression("4 + 3")
        assertEquals("NUMBER(4) PLUS NUMBER(3)", expression.printTokens())
        assertEquals("NUMBER(4) NUMBER(3) PLUS", expression.printReversePolishNotation())
        assertEquals(7, expression.evaluate())
    }

    @Test
    fun testExpressionWithMinus() {
        val expression = Expression("4 - 3")
        assertEquals("NUMBER(4) MINUS NUMBER(3)", expression.printTokens())
        assertEquals("NUMBER(4) NUMBER(3) MINUS", expression.printReversePolishNotation())
        assertEquals(1, expression.evaluate())
    }

    @Test
    fun testExpressionWithMul() {
        val expression = Expression("4 * 3")
        assertEquals("NUMBER(4) MUL NUMBER(3)", expression.printTokens())
        assertEquals("NUMBER(4) NUMBER(3) MUL", expression.printReversePolishNotation())
        assertEquals(12, expression.evaluate())
    }

    @Test
    fun testExpressionWithDiv() {
        val expression = Expression("4 / 3")
        assertEquals("NUMBER(4) DIV NUMBER(3)", expression.printTokens())
        assertEquals("NUMBER(4) NUMBER(3) DIV", expression.printReversePolishNotation())
        assertEquals(1, expression.evaluate())
    }

    @Test
    fun testCompositeExpression() {
        val expression = Expression("6 * 8 - 5 * 5 / 10")
        assertEquals("NUMBER(6) MUL NUMBER(8) MINUS NUMBER(5) MUL NUMBER(5) DIV NUMBER(10)", expression.printTokens())
        assertEquals(
            "NUMBER(6) NUMBER(8) MUL NUMBER(5) NUMBER(5) MUL NUMBER(10) DIV MINUS",
            expression.printReversePolishNotation()
        )
        assertEquals(46, expression.evaluate())
    }

    @Test
    fun testExpressionWithBrackets() {
        val expression = Expression("(84 - 48) * (12 + 10)")
        assertEquals(
            "LEFT NUMBER(84) MINUS NUMBER(48) RIGHT MUL LEFT NUMBER(12) PLUS NUMBER(10) RIGHT",
            expression.printTokens()
        )
        assertEquals(
            "NUMBER(84) NUMBER(48) MINUS NUMBER(12) NUMBER(10) PLUS MUL",
            expression.printReversePolishNotation()
        )
        assertEquals(792, expression.evaluate())
    }

    @Test
    fun testExpressionWithErrorInTheEnd() {
        assertFailsWith<IllegalArgumentException>(
            block = {
                Expression("(84 - 48) * (12 +").evaluate()

            }
        )
    }

    @Test
    fun testExpressionWithErrorInTheBeginning() {
        assertFailsWith<IllegalArgumentException>(
            block = {
                Expression(")84 - 48)").evaluate()
            }
        )
    }

    @Test
    fun testExpressionWithErrorInTheMiddle() {
        assertFailsWith<IllegalArgumentException>(
            block = {
                Expression("(84 -+ 48)").evaluate()
            }
        )
    }

}