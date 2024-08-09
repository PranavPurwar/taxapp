package dev.pranav.myapplication.util

interface TaxRegime {
    val taxSlabs: List<Int>

    fun getTaxRateForIncome(income: Double, age: Age): Int
    fun getTaxRate(slab: Int, age: Age): Int
    fun getTaxRebate(payable: Double, income: Double): Double
    fun getStandardDeductions(income: Double): Double
    fun calculateTax(income: Double, age: Age): Double
}

data object OldRegime : TaxRegime {

    override val taxSlabs = listOf(2_50_000, 3_00_000, 5_00_000, 10_00_000)

    override fun getTaxRate(slab: Int, age: Age): Int = when (slab) {
        2_50_000 -> 0
        3_00_000 -> if (age == Age.SIXTY_OR_LESS) 5 else 0
        5_00_000 -> if (age == Age.EIGHTY_OR_ABOVE) 0 else 5
        10_00_000 -> 20
        else -> 30
    }

    override fun getTaxRateForIncome(income: Double, age: Age): Int = when {
        income <= 2_50_000 -> 0
        income <= 3_00_000 -> if (age == Age.SIXTY_OR_LESS) 5 else 0
        income <= 5_00_000 -> if (age == Age.EIGHTY_OR_ABOVE) 0 else 5
        income <= 10_00_000 -> 20
        else -> 30
    }

    override fun calculateTax(income: Double, age: Age): Double {
        var incomeTax = 0.0

        // Iterate through slabs and calculate tax for each applicable slab.
        for (i in 1 until taxSlabs.size) {
            val slab = taxSlabs[i]
            val previousSlab = taxSlabs[i - 1]
            val taxRate = getTaxRate(slab, age)
            if (income <= slab) {
                incomeTax += (income - previousSlab) * taxRate / 100
                break // Exit loop once income falls within a slab.
            }
            incomeTax += (slab - previousSlab) * taxRate / 100
        }

        if (income - taxSlabs.last() > 0) {
            // Calculate tax for remaining income above the highest slab.
            incomeTax += (income - taxSlabs.last()) * 30 / 100
        }

        return maxOf(incomeTax, 0.0)
    }

    override fun getTaxRebate(payable: Double, income: Double): Double {
        return if (income <= 5_00_000) minOf(payable, 12_500.0) else 0.0
    }

    override fun getStandardDeductions(income: Double): Double {
        return minOf(income, 50_000.0)
    }
}

data object NewRegime : TaxRegime {

    override val taxSlabs = listOf(3_00_000, 6_00_000, 9_00_000, 12_00_000, 15_00_000)

    override fun getTaxRate(slab: Int, age: Age): Int = when (slab) {
        3_00_000 -> 0
        6_00_000 -> 5
        9_00_000 -> 10
        12_00_000 -> 15
        15_00_000 -> 20
        else -> 30
    }

    override fun getTaxRateForIncome(income: Double, age: Age): Int = when {
        income <= 3_00_000 -> 0
        income <= 6_00_000 -> 5
        income <= 9_00_000 -> 10
        income <= 12_00_000 -> 15
        income <= 15_00_000 -> 20
        else -> 30
    }

    override fun calculateTax(income: Double, age: Age): Double {
        var incomeTax = 0.0

        // Iterate through slabs and calculate tax for each applicable slab.
        for (i in 1 until taxSlabs.size) {
            val slab = taxSlabs[i]
            val previousSlab = taxSlabs[i - 1]
            val taxRate = getTaxRate(slab, age)
            if (income <= slab) {
                incomeTax += (income - previousSlab) * taxRate / 100
                break // Exit loop once income falls within a slab.
            }
            incomeTax += (slab - previousSlab) * taxRate / 100
        }

        if (income - taxSlabs.last() > 0) {
            // Calculate tax for remaining income above the highest slab.
            incomeTax += (income - taxSlabs.last()) * 30 / 100
        }

        return maxOf(incomeTax, 0.0)
    }

    override fun getTaxRebate(payable: Double, income: Double): Double = when {
        income <= 7_00_000 -> minOf(payable, 25_000.0)
        income - 7_00_000 < payable -> payable - (income - 7_00_000)
        else -> 0.0
    }

    override fun getStandardDeductions(income: Double): Double {
        return minOf(income, 75_000.0)
    }

}

enum class Age {
    SIXTY_OR_LESS, SIXTY_TO_EIGHTY, EIGHTY_OR_ABOVE
}

enum class Employment {
    SALARIED, PENSIONER, OTHER
}
