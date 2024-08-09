package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.pranav.myapplication.databinding.FragmentIncomeDetailsBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.Employment
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime
import dev.pranav.myapplication.util.Regime
import dev.pranav.myapplication.util.addCurrencyFormatter
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
                deductableInterest,
                if (age == Age.SIXTY_OR_LESS) 10_000.0 else 50_000.0
            )

            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container,
                    DeductionsFragment.newInstance(
                        age,
                        employment,
                        regime,
                        taxableIncome,
                        digitalAssetsIncome
                    )
                )
                commit()
            }

            return@setOnClickListener
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

    override fun toString(): String {
        return "Income Details"
    }
}
