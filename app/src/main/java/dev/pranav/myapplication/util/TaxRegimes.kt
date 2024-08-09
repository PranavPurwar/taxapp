package dev.pranav.myapplication.util

import kotlin.math.min

interface Regime {
    val taxSlabs: List<Int>

    fun calculateInterestRate(income: Double, age: Age): Int
    fun getTaxRate(slab: Int, age: Age): Int
    fun getTaxRebate(payable: Double, income: Double): Double
    fun getStandardDeductions(income: Double): Double
    fun calculateTax(income: Double, age: Age): Double
}

object OldRegime : Regime {

    override val taxSlabs = listOf(2_50_000, 3_00_000, 5_00_000, 10_00_000)

    override fun getTaxRate(slab: Int, age: Age): Int {
        return when (slab) {
            2_50_000 -> 0
            3_00_000 -> {
                if (age == Age.SIXTY_OR_LESS) 5 else 0
            }

            5_00_000 -> {
                if (age == Age.EIGHTY_OR_ABOVE) 0 else 5
            }

            10_00_000 -> 20
            else -> 30
        }
    }

    override fun calculateInterestRate(income: Double, age: Age): Int {
        return when {
            income <= 2_50_000 -> 0
            income <= 3_00_000 -> {
                if (age == Age.SIXTY_OR_LESS) 5 else 0
            }

            income <= 5_00_000 -> {
                if (age == Age.EIGHTY_OR_ABOVE) 0 else 5
            }

            income <= 10_00_000 -> 20
            else -> 30
        }
    }

    override fun calculateTax(income: Double, age: Age): Double {
        var incomeTax = 0.0

        for (i in 1 until taxSlabs.size) {
            val slab = taxSlabs[i]
            val previousSlab = taxSlabs[i - 1]
            val taxRate = getTaxRate(slab, age)
            if (income <= slab) {
                incomeTax += (income - previousSlab) * taxRate / 100
                break
            }
            incomeTax += (slab - previousSlab) * taxRate / 100
        }
        incomeTax += (income - taxSlabs.last()) * NewRegime.getTaxRate(
            taxSlabs.last(),
            age
        ) / 100

        return incomeTax
    }

    override fun getTaxRebate(payable: Double, income: Double): Double {
        return if (income <= 5_00_000) min(payable, 12_500.0) else 0.0
    }

    override fun getStandardDeductions(income: Double): Double {
        return min(income, 50_000.0)
    }

    override fun toString(): String {
        return "OldRegime"
    }
}

object NewRegime : Regime {

    override val taxSlabs = listOf(3_00_000, 6_00_000, 9_00_000, 12_00_000, 15_00_000)

    override fun getTaxRate(slab: Int, age: Age): Int {
        return when (slab) {
            3_00_000 -> 0
            6_00_000 -> 5
            9_00_000 -> 10
            12_00_000 -> 15
            15_00_000 -> 20
            else -> 30
        }
    }

    override fun calculateInterestRate(income: Double, age: Age): Int {
        return when {
            income <= 3_00_000 -> 0
            income <= 6_00_000 -> 5
            income <= 9_00_000 -> 10
            income <= 12_00_000 -> 15
            income <= 15_00_000 -> 20
            else -> 30
        }
    }

    override fun calculateTax(income: Double, age: Age): Double {
        var incomeTax = 0.0

        for (i in 1 until taxSlabs.size) {
            val slab = taxSlabs[i]
            val previousSlab = taxSlabs[i - 1]
            val taxRate = getTaxRate(slab, age)
            if (income <= slab) {
                incomeTax += (income - previousSlab) * taxRate / 100
                break
            }
            incomeTax += (slab - previousSlab) * taxRate / 100
        }
        incomeTax += (income - taxSlabs.last()) * getTaxRate(taxSlabs.last(), age) / 100

        return incomeTax
    }

    override fun getTaxRebate(payable: Double, income: Double): Double {
        return if (income <= 7_00_000) {
            min(payable, 25_000.0)
        } else if (income - 7_00_000 < payable) {
            payable - (income - 7_00_000)
        } else {
            0.0
        }
    }

    override fun getStandardDeductions(income: Double): Double {
        return min(income, 75_000.0)
    }

    override fun toString(): String {
        return "NewRegime"
    }
}

enum class Age {
    SIXTY_OR_LESS,
    SIXTY_TO_EIGHTY,
    EIGHTY_OR_ABOVE
}

enum class Employment {
    SALARIED,
    PENSIONER,
    OTHER
}
