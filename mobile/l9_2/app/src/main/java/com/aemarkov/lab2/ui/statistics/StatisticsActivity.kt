package com.aemarkov.lab2.ui.statistics

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aemarkov.lab2.databinding.ActivityStatisticsBinding
import com.aemarkov.lab2.ui.adapter.StatisticsAdapter
import com.aemarkov.lab2.ui.statistics.StatisticsViewModel.PeriodFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var adapter: StatisticsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        setupCategoryFilter()
        setupPeriodFilters()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = StatisticsAdapter()
        binding.statisticsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.statisticsRecyclerView.adapter = adapter
    }
    
    private fun setupCategoryFilter() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                val categoryNames = listOf("Все категории") + categories.map { it.name }
                val adapter = ArrayAdapter(
                    this@StatisticsActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    categoryNames
                )
                binding.categoryFilterAutoComplete.setAdapter(adapter)
                
                binding.categoryFilterAutoComplete.setOnItemClickListener { _, _, position, _ ->
                    if (position == 0) {
                        viewModel.setCategoryFilter(null)
                    } else {
                        viewModel.setCategoryFilter(categories[position - 1].name)
                    }
                }
            }
        }
    }
    
    private fun setupPeriodFilters() {
        binding.dayChip.setOnClickListener {
            viewModel.setPeriodFilter(PeriodFilter.DAY)
        }
        
        binding.weekChip.setOnClickListener {
            viewModel.setPeriodFilter(PeriodFilter.WEEK)
        }
        
        binding.monthChip.setOnClickListener {
            viewModel.setPeriodFilter(PeriodFilter.MONTH)
        }
        
        binding.yearChip.setOnClickListener {
            viewModel.setPeriodFilter(PeriodFilter.YEAR)
        }
        
        // Set default to month
        binding.monthChip.isChecked = true
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
        
        lifecycleScope.launch {
            viewModel.statistics.collect { statistics ->
                statistics?.let {
                    binding.totalAmountText.text = "${it.totalAmount} ₽"
                    binding.expenseCountText.text = "${it.expenseCount} расходов"
                    adapter.submitList(it.byCategory)
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(this@StatisticsActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.selectedPeriod.collect { period ->
                // Uncheck all chips first
                binding.dayChip.isChecked = false
                binding.weekChip.isChecked = false
                binding.monthChip.isChecked = false
                binding.yearChip.isChecked = false
                
                // Check the selected chip
                when (period) {
                    PeriodFilter.DAY -> binding.dayChip.isChecked = true
                    PeriodFilter.WEEK -> binding.weekChip.isChecked = true
                    PeriodFilter.MONTH -> binding.monthChip.isChecked = true
                    PeriodFilter.YEAR -> binding.yearChip.isChecked = true
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

