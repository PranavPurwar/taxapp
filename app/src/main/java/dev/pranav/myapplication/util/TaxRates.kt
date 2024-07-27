package dev.pranav.myapplication.util

object OldRegime {
    fun calculateInterestRate(income: Double, age: Age): Int {
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

    fun getTaxRebate(payable: Double): Double {
        return if (payable <= 12_500) payable else 0.0
    }
}

object NewRegime {
    fun calculateInterestRate(income: Double): Int {
        return when {
            income <= 3_00_000 -> 0
            income <= 6_00_000 -> 5
            income <= 9_00_000 -> 10
            income <= 12_00_000 -> 15
            income <= 15_00_000 -> 20
            else -> 30
        }
    }

    fun getTaxRebate(payable: Double): Double {
        return if (payable <= 25_000) payable else 0.0
    }
}

enum class Age {
    SIXTY_OR_LESS,
    SIXTY_TO_EIGHTY,
    EIGHTY_OR_ABOVE
}
