package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.pranav.myapplication.databinding.PayableTaxBottomSheetBinding
import dev.pranav.myapplication.databinding.TaxCalcuateFragmentBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Helper
import dev.pranav.myapplication.util.Helper.toINRString
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime

class TaxCalculateFragment : Fragment() {

    private var _binding: TaxCalcuateFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = TaxCalcuateFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calculateButton.setOnClickListener {
            val income = binding.incomeEditText.text.toString().toDouble()
            val age = when (binding.ageGroup.checkedButtonId) {
                R.id.eighteen_to_sixty -> Age.SIXTY_OR_LESS
                R.id.sixty_to_eighty -> Age.SIXTY_TO_EIGHTY
                R.id.eighty_or_above -> Age.EIGHTY_OR_ABOVE
                else -> Age.SIXTY_OR_LESS
            }
            val employment = when (binding.employmentGroup.checkedButtonId) {
                R.id.salaried -> dev.pranav.myapplication.util.Employment.SALARIED
                R.id.pensioners -> dev.pranav.myapplication.util.Employment.PENSIONER
                else -> dev.pranav.myapplication.util.Employment.OTHER
            }
            val regime =
                if (binding.buttonGroup.checkedButtonId == R.id.old_regime) OldRegime else NewRegime
            val taxRate = regime.calculateInterestRate(income, age)
            val taxAmount = Helper.calculateTax(
                regime,
                employment,
                income,
                binding.deductibleIncome.text.toString().toDouble(),
                binding.digitalAssetsIncome.text.toString().toDouble(),
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
}
