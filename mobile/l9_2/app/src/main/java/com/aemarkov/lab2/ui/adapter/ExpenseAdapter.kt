package com.aemarkov.lab2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab2.R
import com.aemarkov.lab2.data.models.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        private val dateText: TextView = itemView.findViewById(R.id.dateText)
        
        fun bind(expense: Expense) {
            categoryText.text = expense.category
            amountText.text = "${expense.amount} ₽"
            descriptionText.text = expense.description ?: "Без описания"
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            try {
                val date = dateFormat.parse(expense.date)
                dateText.text = date?.let { displayFormat.format(it) } ?: expense.date
            } catch (e: Exception) {
                dateText.text = expense.date
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

