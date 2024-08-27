package dev.pranav.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import dev.pranav.myapplication.databinding.FragmentSettingsBinding
import dev.pranav.myapplication.util.setLocale
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)

        val prefs = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE)

        binding.languageSelection.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Select Language")
                .setSingleChoiceItems(
                    arrayOf("English", "Hindi"), prefs.getInt("language_int", 0)
                ) { dialog, which ->
                    prefs.edit().putInt("language_int", which).apply()
                    val locale = when (which) {
                        0 -> Locale("en")
                        1 -> Locale("hi")
                        else -> Locale("en")
                    }
                    prefs.edit().putString("language", locale.language).apply()
                    requireActivity().setLocale(locale)
                    requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
                        .setTitle(R.string.action_settings)
                    dialog.dismiss()
                    parentFragmentManager
                        .beginTransaction()
                        .detach(this)
                        .commit()

                    parentFragmentManager.beginTransaction()
                        .attach(this)
                        .commit()
                }.show()
        }
        binding.faq.setOnClickListener {
            val intent = Intent().setAction(Intent.ACTION_VIEW)
                .setData(Uri.parse("mailto:purwarpranav80@gmail.com"))

            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun toString(): String {
        return getString(R.string.action_settings)
    }
}
