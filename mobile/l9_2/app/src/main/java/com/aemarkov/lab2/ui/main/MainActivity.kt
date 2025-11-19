package com.aemarkov.lab2.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aemarkov.lab2.databinding.ActivityMainBinding
import com.aemarkov.lab2.ui.add.AddExpenseActivity
import com.aemarkov.lab2.ui.adapter.ExpenseAdapter
import com.aemarkov.lab2.ui.statistics.StatisticsActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        setupRecyclerView()
        setupSearch()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = ExpenseAdapter()
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.expensesRecyclerView.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.searchEditText.setOnTextChangedListener { text ->
            viewModel.setSearchQuery(text.toString())
        }
    }
    
    private fun setupButtons() {
        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        
        binding.statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        lifecycleScope.launch {
            viewModel.filteredExpenses.collect { expenses ->
                adapter.submitList(expenses)
            }
        }
        
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadExpenses()
    }
    
    private fun TextInputEditText.setOnTextChangedListener(
        onTextChanged: (String) -> Unit
    ) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                onTextChanged(s?.toString() ?: "")
            }
        })
    }
}

