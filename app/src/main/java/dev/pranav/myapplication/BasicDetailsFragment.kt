package dev.pranav.myapplication

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import dev.pranav.myapplication.databinding.FragmentBasicDetailsBinding
import dev.pranav.myapplication.util.Age
import dev.pranav.myapplication.util.NewRegime
import dev.pranav.myapplication.util.OldRegime

class BasicDetailsFragment : Fragment() {

    private var _binding: FragmentBasicDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        binding.buttonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.new_regime && isChecked) {
                binding.ageLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.space.visibility = View.GONE
            } else {
                binding.space.visibility = View.VISIBLE
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.ageLayout.visibility = View.VISIBLE
            }
        }

        binding.continueButton.setOnClickListener {
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

            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container,
                    IncomeDetailsFragment.newInstance(age, employment, regime)
                )
                addToBackStack(null)
            }.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun toString(): String {
        return getString(R.string.basic_details)
    }
}
