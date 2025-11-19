package com.aemarkov.lab2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab2.R
import com.aemarkov.lab2.data.models.CategoryStatistics

class StatisticsAdapter : ListAdapter<CategoryStatistics, StatisticsAdapter.StatisticsViewHolder>(
    StatisticsDiffCallback()
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statistics, parent, false)
        return StatisticsViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val countText: TextView = itemView.findViewById(R.id.countText)
        
        fun bind(statistics: CategoryStatistics) {
            categoryText.text = statistics.category
            amountText.text = "${statistics.amount} ₽"
            countText.text = "${statistics.count} расходов"
        }
    }
    
    class StatisticsDiffCallback : DiffUtil.ItemCallback<CategoryStatistics>() {
        override fun areItemsTheSame(
            oldItem: CategoryStatistics,
            newItem: CategoryStatistics
        ): Boolean {
            return oldItem.category == newItem.category
        }
        
        override fun areContentsTheSame(
            oldItem: CategoryStatistics,
            newItem: CategoryStatistics
        ): Boolean {
            return oldItem == newItem
        }
    }
}

