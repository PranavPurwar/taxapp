package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.pranav.myapplication.databinding.FragmentIncomeDetailsBinding
import dev.pranav.myapplication.databinding.PayableTaxBottomSheetBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Employment
import dev.pranav.myapplication.util.Helper
import dev.pranav.myapplication.util.Helper.addCurrencyFormatter
import dev.pranav.myapplication.util.Helper.toINRString
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime
import dev.pranav.myapplication.util.Regime

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

        binding.calculateButton.setOnClickListener {
            binding.apply {
                if (binding.incomeEditText.text.toString().isEmpty()) {
                    binding.incomeInput.error = "Please enter your income"
                    return@setOnClickListener
                }
                if (binding.deductibleIncome.text.toString().isEmpty()) {
                    binding.deductibleInput.error = "Please enter your income"
                    return@setOnClickListener
                }
                if (binding.digitalAssetsIncome.text.toString().isEmpty()) {
                    binding.digitalInput.error = "Please enter your income"
                    return@setOnClickListener
                }
            }
            val income = binding.incomeEditText.text.toString().toDouble()
            val deductableInterest = binding.deductibleIncome.text.toString().toDouble()
            val digitalAssetsIncome = binding.digitalAssetsIncome.text.toString().toDouble()
            val taxRate = regime.calculateInterestRate(income, age)
            val taxAmount = Helper.calculateTax(
                regime,
                employment,
                income,
                deductableInterest,
                digitalAssetsIncome,
                age
            )

            val sheet = BottomSheetDialog(requireContext())
            val sheetBinding = PayableTaxBottomSheetBinding.inflate(layoutInflater)

            sheetBinding.apply {
                annualIncome.text = "+" + income.toINRString()
                digitalIncome.text =
                    binding.digitalAssetsIncome.text.toString().toDouble().toINRString()
                deductibleIncome.text =
                    binding.deductibleIncome.text.toString().toDouble().toINRString()
                this.taxRate.text = taxRate.toString() + "%"
                this.taxAmount.text = taxAmount.toString().toDouble().toINRString()
                this.taxRebate.text = regime.getTaxRebate(taxAmount).toINRString()
                this.payableTax.text = (taxAmount - regime.getTaxRebate(taxAmount)).toINRString()
            }
            sheet.setContentView(sheetBinding.root)
            sheet.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(age: Age, employment: Employment, regime: Regime): IncomeDetailsFragment {
            val args = Bundle()
            args.putSerializable("age", age)
            args.putSerializable("employment", employment)
            args.putSerializable("regime", regime.toString())
            val fragment = IncomeDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
