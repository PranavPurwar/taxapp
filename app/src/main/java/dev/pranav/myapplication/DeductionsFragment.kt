package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.pranav.myapplication.databinding.FragmentDeductionsBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Employment
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime
import dev.pranav.myapplication.util.TaxRegime
import dev.pranav.myapplication.util.addCurrencyFormatter
import dev.pranav.myapplication.util.calculateHealthAndEducationalCess
import dev.pranav.myapplication.util.getCurrencyValue
import kotlin.math.min

class DeductionsFragment : Fragment() {

    private var _binding: FragmentDeductionsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeductionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val age = arguments?.getSerializable("age")!! as Age
        val employment = arguments?.getSerializable("employment")!! as Employment
        val regime = when (arguments?.getSerializable("regime")!!) {
            "OldRegime" -> OldRegime
            "NewRegime" -> NewRegime
            else -> OldRegime
        }
        val taxableIncome = arguments?.getDouble("taxableIncome")!!
        val digitalAssetsIncome = arguments?.getDouble("digitalAssetsIncome")!!

        binding.apply {
            if (regime == NewRegime) {
                cSection.visibility = View.GONE
                dSection.visibility = View.GONE
                eSection.visibility = View.GONE
                gSection.visibility = View.GONE
                npsEmployer.visibility = View.GONE
            }

            investment.addCurrencyFormatter()
            medicalInsurance.addCurrencyFormatter()
            educationLoan.addCurrencyFormatter()
            charityDonation.addCurrencyFormatter()
            npsContribution.addCurrencyFormatter()

            calculateButton.setOnClickListener {
                val deductions80C = min(investment.getCurrencyValue(), 1_50_000.0)
                val deductions80D = min(
                    medicalInsurance.getCurrencyValue(),
                    if (age == Age.SIXTY_OR_LESS) 25_000.0 else 50_000.0
                )
                val deductions80E = educationLoan.getCurrencyValue()
                val deductions80G = charityDonation.getCurrencyValue()
                val npsContribution = min(npsContribution.getCurrencyValue(), 50_000.0)

                val standardDeduction = regime.getStandardDeductions(taxableIncome)

                val taxableAmount =
                    taxableIncome - deductions80C - deductions80D - deductions80E - deductions80G - npsContribution - standardDeduction

                val initialTax = regime.calculateTax(taxableAmount, age)
                var tax = initialTax
                val cess = calculateHealthAndEducationalCess(tax)
                val rebate = regime.getTaxRebate(tax, taxableIncome)
                val digitalAssetsTax = digitalAssetsIncome * 0.03

                tax += cess + digitalAssetsTax - rebate

                val map = mutableMapOf<String, Double>()
                map.apply {
                    put("Annual Income", taxableIncome)
                    put("Digital Assets Income", digitalAssetsIncome)
                    put("Investments and Expenses (80C)", -deductions80C)
                    put("Medical Insurance (80D)", -deductions80D)
                    put("Education Loan (80E)", -deductions80E)
                    put("Charity Donation (80G)", -deductions80G)
                    put("National Pension Scheme Contribution (NPS)", -npsContribution)
                    put("Standard Deductions", -standardDeduction)
                    put("Taxable Amount", taxableAmount)
                    put("Digital Assets Tax", digitalAssetsTax)
                    put("Health and Education Cess", cess)
                    put("Tax Rebate", -rebate)
                }

                TaxSheetFragment(
                    map, tax,
                    OldRegime.calculateTax(taxableAmount, age)
                        .let { if (it < initialTax) "Old regime can save you up to ₹${initialTax - it}" else "" }
                ).show(parentFragmentManager, "TaxSheetFragment")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            age: Age,
            employment: Employment,
            regime: TaxRegime,
            taxableIncome: Double,
            digitalAssetsIncome: Double
        ): DeductionsFragment {
            val args = Bundle()
            args.putSerializable("age", age)
            args.putSerializable("employment", employment)
            args.putSerializable("regime", regime.toString())
            args.putDouble("taxableIncome", taxableIncome)
            args.putDouble("digitalAssetsIncome", digitalAssetsIncome)

            val fragment = DeductionsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun toString(): String {
        return "Deductions"
    }
}
