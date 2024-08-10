package dev.pranav.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.pranav.myapplication.databinding.TaxArticleListItemBinding
import dev.pranav.myapplication.databinding.TaxPlanningBinding

class PlanningFragment : Fragment() {

    private var _binding: TaxPlanningBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = TaxPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = ArticleAdapter(data)
        binding.list.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun toString(): String {
        return "Tax Planning"
    }
}

internal class ArticleAdapter(val data: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    inner class ViewHolder(val itemBinding: TaxArticleListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TaxArticleListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.title.text = data[position].title
        holder.itemBinding.description.text = data[position].description
        holder.itemBinding.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data[position].url))
            startActivity(holder.itemBinding.root.context, intent, null)
        }
    }
}

val data = listOf(
    Article(
        "What is Tax Planning?",
        "Tax Planning is a way of minimising your tax liabilities by arranging your finances in such a way that can take advantage of available tax deductions, benefits, and exemptions given by the government to reduce your net taxable income effectively.",
        "https://www.fincart.com/blog/what-is-tax-planning"
    ),
    Article(
        "Income Tax Saving ways for beginners",
        "Tax planning is one of the ways that will help you save on taxes and increase your income. The Income Tax Act provides deductions for various investments, savings and expenditures incurred by the taxpayer in a particular financial year.",
        "https://cleartax.in/s/income-tax-savings"
    )
)

data class Article(val title: String, val description: String, val url: String)
