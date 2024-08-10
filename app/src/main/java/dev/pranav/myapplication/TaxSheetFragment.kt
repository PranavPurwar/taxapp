package dev.pranav.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.pranav.myapplication.databinding.FragmentTaxSheetDialogBinding
import dev.pranav.myapplication.databinding.FragmentTaxSheetListDialogItemBinding
import dev.pranav.myapplication.util.toINRString

class TaxSheetFragment(
    private val mItems: Map<String, Double>,
    private val payableTax: Double,
    private val message: String = ""
) : BottomSheetDialogFragment() {

    private var _binding: FragmentTaxSheetDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaxSheetDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager =
            LinearLayoutManager(context)
        binding.list.adapter =
            TitleValueAdapter(mItems).apply {
                notifyDataSetChanged()
            }
        binding.payableTax.text = payableTax.toINRString()

        if (message.isNotEmpty()) {
            binding.regimeMessage.visibility = View.VISIBLE
            binding.regimeMessage.text = message
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
        return "Tax Sheet"
    }
}
