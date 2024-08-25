package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.pranav.myapplication.databinding.ActivityTaxBinding
import dev.pranav.myapplication.databinding.FragmentTaxSheetListDialogItemBinding
import dev.pranav.myapplication.util.toINRString

class TaxFragment(
    private val incomeSources: Map<String, Double>,
    private val deductions: Map<String, Double>,
    private val tax: Map<String, Double>,
    private val payableTax: Double,
    private val message: String = ""
) : Fragment() {

    private var _binding: ActivityTaxBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ActivityTaxBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.income.layoutManager =
            LinearLayoutManager(context)
        binding.income.adapter =
            TitleValueAdapter(incomeSources).apply {
                notifyDataSetChanged()
            }
        binding.deductions.layoutManager =
            LinearLayoutManager(context)
        binding.deductions.adapter =
            TitleValueAdapter(deductions).apply {
                notifyDataSetChanged()
            }
        binding.tax.layoutManager =
            LinearLayoutManager(context)
        binding.tax.adapter =
            TitleValueAdapter(tax).apply {
                notifyDataSetChanged()
            }
        binding.payableTax.text = payableTax.toINRString()

        if (message.isNotEmpty()) {
            Snackbar.make(
                binding.root, message, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private inner class ViewHolder(binding: FragmentTaxSheetListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val title: TextView = binding.title
        val value: TextView = binding.value
    }

    private inner class TitleValueAdapter(private val mItems: Map<String, Double>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentTaxSheetListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val key = mItems.keys.elementAt(position)
            holder.title.text = key
            holder.value.text = mItems[key]?.toINRString()
        }

        override fun getItemCount(): Int {
            return mItems.count()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun toString(): String {
        return "Tax Details"
    }
}
