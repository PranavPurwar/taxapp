package dev.pranav.myapplication.util

fun main(args: Array<String>) {
    println("Enter your income")
    val income = readLine()!!.toDouble()

    println("Your taxable income is $income")
    val payable = NewRegime.calculateTax(income, Age.SIXTY_OR_LESS)

    println("Your payable tax is $payable")
    println("Health and educational cess is ${calculateHealthAndEducationalCess(payable)}")
}
