package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dev.pranav.myapplication.databinding.FragmentIncomeDetailsBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Employment
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime
import dev.pranav.myapplication.util.TaxRegime
import dev.pranav.myapplication.util.addCurrencyFormatter
import dev.pranav.myapplication.util.calculateHealthAndEducationalCess
import dev.pranav.myapplication.util.getCurrencyValue
import kotlin.math.min

class IncomeDetailsFragment : Fragment() {

    private var _binding: FragmentIncomeDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val age = arguments?.getSerializable("age") as Age
        val employment = arguments?.getSerializable("employment") as Employment
        val regime = when (arguments?.getSerializable("regime")) {
            "OldRegime" -> OldRegime
            "NewRegime" -> NewRegime
            else -> OldRegime
        }
        binding.incomeEditText.addCurrencyFormatter()
        binding.deductibleIncome.addCurrencyFormatter()
        binding.digitalAssetsIncome.addCurrencyFormatter()

        binding.continueButton.setOnClickListener {
            if (binding.incomeEditText.text.toString().isEmpty()) {
                binding.incomeInput.error = "Please enter your income"
                return@setOnClickListener
            }

            val income = binding.incomeEditText.getCurrencyValue()
            val deductableInterest = binding.deductibleIncome.getCurrencyValue()
            val digitalAssetsIncome = binding.digitalAssetsIncome.getCurrencyValue()

            val taxableIncome = income - min(
                deductableInterest, if (age == Age.EIGHTY_OR_ABOVE) 50_000.0 else 10_000.0
            )

            if (regime == OldRegime) {
                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_container, DeductionsFragment.newInstance(
                            age, employment, regime, taxableIncome, digitalAssetsIncome
                        )
                    )
                    commit()
                }
            } else {
                val standardDeduction = regime.getStandardDeductions(taxableIncome)

                val taxableAmount = taxableIncome - standardDeduction

                val initialTax = regime.calculateTax(taxableAmount, age)
                var tax = initialTax
                Snackbar.make(
                    binding.root, "Calculate Tax: $tax", Snackbar.LENGTH_LONG
                ).show()
                val cess = calculateHealthAndEducationalCess(tax)
                val rebate = regime.getTaxRebate(tax, taxableIncome)
                val digitalAssetsTax = digitalAssetsIncome * 0.03

                tax += cess + digitalAssetsTax - rebate

                val map = mutableMapOf<String, Double>()
                map.apply {
                    put("Annual Income", taxableIncome)
                    put("Digital Assets Income", digitalAssetsIncome)
                    put("Standard Deductions", standardDeduction)
                    put("Taxable Amount", taxableAmount)
                    put("Digital Assets Tax", digitalAssetsTax)
                    put("Health and Education Cess", cess)
                    put("Tax Rebate", -rebate)
                }

                TaxSheetFragment(
                    map, tax,
                    NewRegime.calculateTax(taxableAmount, age)
                        .let { if (it < initialTax) "New Regime can save you up to ₹${it - initialTax}" else "" }
                ).show(parentFragmentManager, "TaxSheetFragment")
            }

            return@setOnClickListener
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            age: Age, employment: Employment, regime: TaxRegime
        ): IncomeDetailsFragment {
            val args = Bundle()
            args.putSerializable("age", age)
            args.putSerializable("employment", employment)
            args.putSerializable("regime", regime.toString())
            val fragment = IncomeDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun toString(): String {
        return "Income Details"
    }
}
