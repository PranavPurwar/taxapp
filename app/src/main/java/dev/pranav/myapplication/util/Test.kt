package dev.pranav.myapplication.util

fun main(args: Array<String>) {
    println("Enter your income")
    val income = readLine()!!.toDouble()

    println("Enter your deductions")
    val deductions = readLine()!!.toDouble()

    val taxableIncome = income - deductions

    println("Enter your digital assets income")
    val digitalAssetsIncome = readLine()!!.toDouble()

    val digitalAssetsTax = digitalAssetsIncome * 0.3

    println("Your taxable income is $taxableIncome")
    println("Your digital assets tax is $digitalAssetsTax")
    val payable = OldRegime.calculateTax(taxableIncome, Age.SIXTY_OR_LESS) + digitalAssetsTax

    println("Your payable tax is $payable")
    println("Health and educational cess is ${calculateHealthAndEducationalCess(payable)}")
}
