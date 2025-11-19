package com.aemarkov.lab9

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab9.adapter.CategoryAdapter
import com.aemarkov.lab9.databinding.ActivityStatisticsBinding
import com.aemarkov.lab9.model.Statistics
import com.aemarkov.lab9.util.DateUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var categoryAdapter: CategoryAdapter
    
    private val periods = listOf(
        "День",
        "Неделя",
        "Месяц",
        "Год"
    )
    
    private val periodValues = mapOf(
        "День" to "day",
        "Неделя" to "week",
        "Месяц" to "month",
        "Год" to "year"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.statistics_title)
        
        setupRecyclerView()
        setupViews()
        loadStatistics()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter()
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = categoryAdapter
    }
    
    private fun setupViews() {
        val periodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = periodAdapter
        
        binding.spinnerPeriod.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadStatistics()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
    
    private fun loadStatistics() {
        binding.progressIndicator.visibility = View.VISIBLE
        
        val selectedPeriod = binding.spinnerPeriod.selectedItem as? String
        val periodValue = periodValues[selectedPeriod] ?: "month"
        
        lifecycleScope.launch {
            try {
                val response = AppData.apiService.getStatistics(period = periodValue)
                if (response.isSuccessful) {
                    val statistics: Statistics? = response.body()
                    statistics?.let {
                        displayStatistics(it)
                    }
                } else {
                    showError("Ошибка загрузки статистики")
                }
            } catch (e: Exception) {
                showError("Ошибка: ${e.message}")
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }
    
    private fun displayStatistics(statistics: Statistics) {
        val numberFormat = DecimalFormat("#,###.00")
        
        // Общая сумма
        binding.tvTotalAmount.text = "${numberFormat.format(statistics.totalAmount)} ₽"
        
        // Количество расходов
        val countText = if (statistics.expenseCount == 1) "расход" else "расходов"
        binding.tvExpenseCount.text = "${statistics.expenseCount} $countText"
        
        // Статистика по категориям
        val categories = statistics.byCategory.sortedByDescending { it.amount }
        categoryAdapter.submitList(categories)
        
        // Устанавливаем максимальную сумму для прогресс-баров
        val maxAmount = categories.maxOfOrNull { it.amount } ?: 0.0
        categoryAdapter.setMaxAmount(maxAmount)
        
        // Показываем/скрываем пустое состояние
        binding.tvEmptyState.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

