package dev.pranav.myapplication.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

object Helper {
    fun calculateTax(
        regime: Regime,
        employment: Employment,
        income: Double,
        deductableInterest: Double,
        digitalAssetsIncome: Double,
        age: Age
    ): Double {
        val interestRate = regime.calculateInterestRate(income, age)
        val taxableIncome = income - deductableInterest
        val digitalAssetsTax = digitalAssetsIncome * 0.3
        val payable = taxableIncome * (interestRate / 100.0) + digitalAssetsTax
        return payable - regime.getTaxRebate(payable)
    }

    fun Double.toINRString(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        return formatter.format(this)
    }

    fun EditText.addCurrencyFormatter() {
        this.addTextChangedListener(object : TextWatcher {

            private var current = ""

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    this@addCurrencyFormatter.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("\\D".toRegex(), "")
                    val parsed = if (cleanString.isBlank()) 0.0 else cleanString.toDouble()
                    val formated = NumberFormat.getCurrencyInstance()
                        .format(parsed / 100).substring(1)

                    current = formated
                    this@addCurrencyFormatter.setText(formated)
                    this@addCurrencyFormatter.setSelection(formated.length)

                    this@addCurrencyFormatter.addTextChangedListener(this)
                }
            }
        })
    }
}
