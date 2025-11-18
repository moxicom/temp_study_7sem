package com.aemarkov.lab9.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab9.R
import com.aemarkov.lab9.model.Expense
import com.aemarkov.lab9.util.DateUtils
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit,
    private val onItemLongClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        
        private val numberFormat = DecimalFormat("#,###.00")

        fun bind(expense: Expense) {
            tvCategory.text = expense.category
            tvAmount.text = "${numberFormat.format(expense.amount)} ₽"
            tvDescription.text = expense.description ?: ""
            tvDate.text = DateUtils.formatForDisplay(expense.date)
            
            itemView.setOnClickListener {
                onItemClick(expense)
            }
            
            itemView.setOnLongClickListener {
                onItemLongClick(expense)
                true
            }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}

