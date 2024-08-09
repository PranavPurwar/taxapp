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
import dev.pranav.myapplication.util.Regime
import dev.pranav.myapplication.util.addCurrencyFormatter

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
        val age = arguments?.getSerializable("age") as Age
        val employment = arguments?.getSerializable("employment") as Employment
        val regime = when (arguments?.getSerializable("regime")) {
            "OldRegime" -> OldRegime
            "NewRegime" -> NewRegime
            else -> OldRegime
        }
        val taxableIncome = arguments?.getDouble("taxableIncome")
        val digitalAssetsIncome = arguments?.getDouble("digitalAssetsIncome")

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
            regime: Regime,
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
        return "Income Details"
    }
}
