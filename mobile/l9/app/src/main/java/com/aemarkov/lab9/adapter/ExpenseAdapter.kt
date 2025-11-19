package com.aemarkov.lab9.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab9.R
import com.aemarkov.lab9.model.Expense
import com.aemarkov.lab9.util.DateUtils
import java.text.DecimalFormat

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit,
    private val onItemLongClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses: List<Expense> = emptyList()

    fun submitList(newList: List<Expense>) {
        expenses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size

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
            tvDate.text = DateUtils.formatForCard(expense.date)
            
            itemView.setOnClickListener {
                onItemClick(expense)
            }
            
            itemView.setOnLongClickListener {
                onItemLongClick(expense)
                true
            }
        }
    }
}

