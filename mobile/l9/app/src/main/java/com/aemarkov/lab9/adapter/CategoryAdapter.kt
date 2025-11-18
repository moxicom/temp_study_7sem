package com.aemarkov.lab9.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab9.R
import com.aemarkov.lab9.model.CategoryStatistics
import java.text.DecimalFormat

class CategoryAdapter : ListAdapter<CategoryStatistics, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var maxAmount: Double = 0.0
    private val numberFormat = DecimalFormat("#,###.00")

    fun setMaxAmount(max: Double) {
        maxAmount = max
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), maxAmount)
    }

    inner class CategoryViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvCategoryAmount: TextView = itemView.findViewById(R.id.tvCategoryAmount)
        private val tvCategoryCount: TextView = itemView.findViewById(R.id.tvCategoryCount)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(category: CategoryStatistics, maxAmount: Double) {
            tvCategoryName.text = category.category
            tvCategoryAmount.text = "${numberFormat.format(category.amount)} ₽"
            val countText = if (category.count == 1) "расход" else "расходов"
            tvCategoryCount.text = "${category.count} $countText"
            
            if (maxAmount > 0) {
                val progress = ((category.amount / maxAmount) * 100).toInt()
                progressBar.progress = progress
            } else {
                progressBar.progress = 0
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryStatistics>() {
        override fun areItemsTheSame(oldItem: CategoryStatistics, newItem: CategoryStatistics): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: CategoryStatistics, newItem: CategoryStatistics): Boolean {
            return oldItem == newItem
        }
    }
}

