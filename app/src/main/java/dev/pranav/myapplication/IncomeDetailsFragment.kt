package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import dev.pranav.myapplication.databinding.FragmentIncomeDetailsBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Employment
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime
import dev.pranav.myapplication.util.TaxRegime
import dev.pranav.myapplication.util.addCurrencyFormatter
import dev.pranav.myapplication.util.calculateHealthAndEducationalCess
import dev.pranav.myapplication.util.getCurrencyValue
import dev.pranav.myapplication.util.toINRString

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

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

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

            val taxableIncome = income - deductableInterest

            if (regime == OldRegime) {
                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_container, DeductionsFragment.newInstance(
                            age, employment, regime, taxableIncome, digitalAssetsIncome
                        )
                    )
                    addToBackStack(null)
                    commit()
                }
            } else {
                val standardDeduction = regime.getStandardDeductions(taxableIncome)

                val taxableAmount = taxableIncome - standardDeduction

                val initialTax = regime.calculateTax(taxableAmount, age)
                var tax = initialTax
                val cess = calculateHealthAndEducationalCess(tax)
                val rebate = regime.getTaxRebate(tax, taxableIncome)
                val digitalAssetsTax = digitalAssetsIncome * 0.03
                val additonalTaxes = regime.getAdditionalTaxes(taxableIncome, age)

                tax += cess + digitalAssetsTax + additonalTaxes - rebate

                val incomeSources = mutableMapOf(
                    getString(R.string.annual_income) to income,
                    getString(R.string.digital_assets_income) to digitalAssetsIncome,
                )

                val deductions = mutableMapOf(
                    getString(R.string.standard_deduction) to standardDeduction,
                )

                val taxed = mutableMapOf(
                    getString(R.string.taxable_income) to taxableAmount,
                    getString(R.string.digital_assets_tax) to digitalAssetsTax,
                    getString(R.string.health_and_education_cess) to cess,
                    getString(R.string.additional_taxes) to additonalTaxes,
                    getString(R.string.tax_rebate) to rebate,
                )

                val oldTax = OldRegime.calculateTax(taxableAmount, age)
                val additonalTaxesOld = OldRegime.getAdditionalTaxes(taxableIncome, age)
                val totalOldTax = oldTax + additonalTaxesOld + digitalAssetsTax

                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_container, TaxFragment(
                            incomeSources, deductions, taxed, tax,
                            if (totalOldTax < tax)
                                getString(R.string.old_regime) + " " + getString(
                                    R.string.can_save_you,
                                    (tax - totalOldTax).toINRString()
                                ) else ""
                        )
                    )
                }.commit()
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
        return getString(R.string.income_details)
    }
}
