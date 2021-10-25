import expression.Expression

fun main() {
    Expression("5 * 4 + 3 * 8").getAllInformation()
    println("\n----------------------------------------\n")
    Expression("6 * 8 - 5 * 5 / 10").getAllInformation()
    println("\n----------------------------------------\n")
    Expression("(84 - 48) * (12 + 10)").getAllInformation()
}