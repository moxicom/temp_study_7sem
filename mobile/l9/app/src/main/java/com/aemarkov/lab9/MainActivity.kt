package com.aemarkov.lab9

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemarkov.lab9.adapter.ExpenseAdapter
import com.aemarkov.lab9.databinding.ActivityMainBinding
import com.aemarkov.lab9.model.Expense
import com.aemarkov.lab9.util.DateUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categorySpinner: Spinner
    private lateinit var searchEditText: TextInputEditText
    
    private var allExpenses: List<Expense> = emptyList()
    private var selectedCategory: String? = null
    
    private val categories = listOf(
        "Все категории",
        "Продукты",
        "Транспорт",
        "Развлечения",
        "Покупки",
        "Счета",
        "Здоровье",
        "Другое"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setSupportActionBar(binding.toolbar)
            
            setupRecyclerView()
            setupViews()

            // с небольшой задержкой, чтобы UI успел инициализироваться
            binding.root.post {
                loadExpenses()
            }
        } catch (e: Exception) {
            // Логируем ошибку для отладки
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            throw e
        }
    }
    
    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense ->
                // Открыть экран редактирования
                val intent = Intent(this, AddEditExpenseActivity::class.java)
                intent.putExtra("expense_id", expense.id)
                startActivity(intent)
            },
            onItemLongClick = { expense ->
                showDeleteDialog(expense)
            }
        )
        
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = expenseAdapter
    }
    
    private fun setupViews() {
        // Настройка Spinner категорий
        categorySpinner = binding.spinnerCategory
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        
        categorySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = if (position == 0) null else categories[position]
                // Задержка, чтобы избежать вызова до полной инициализации
                binding.root.post {
                    filterExpenses()
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Настройка поиска
        searchEditText = binding.etSearch
        searchEditText.setOnEditorActionListener { _, _, _ ->
            filterExpenses()
            true
        }
        
        // Кнопка фильтра по дате
        binding.btnFilterDate.setOnClickListener {
            showDateFilterDialog()
        }
        
        // FAB для добавления расхода
        binding.fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddEditExpenseActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun loadExpenses() {
        if (!::binding.isInitialized) {
            return
        }
        
        binding.progressIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = AppData.apiService.getExpenses()
                if (response.isSuccessful) {
                    allExpenses = response.body()?.expenses ?: emptyList()
                    filterExpenses()
                    updateTotalAmount()
                } else {
                    // Показываем ошибку только если это не первый запуск
                    if (allExpenses.isNotEmpty()) {
                        showError(getString(R.string.sync_error))
                    }
                }
            } catch (e: Exception) {
                // Не показываем ошибку при первом запуске, если API недоступен
                if (allExpenses.isNotEmpty()) {
                    showError("Ошибка: ${e.message}")
                }
            } finally {
                if (::binding.isInitialized) {
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }
    
    private fun filterExpenses() {
        if (!::searchEditText.isInitialized) {
            return
        }
        
        val searchQuery = searchEditText.text?.toString()?.lowercase() ?: ""
        
        var filtered = allExpenses
        
        // Фильтр по категории
        if (selectedCategory != null) {
            filtered = filtered.filter { it.category == selectedCategory }
        }
        
        // Фильтр по поисковому запросу
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.description?.lowercase()?.contains(searchQuery) == true ||
                it.category.lowercase().contains(searchQuery)
            }
        }
        
        if (::expenseAdapter.isInitialized) {
            expenseAdapter.submitList(filtered)
        }
        
        // Показываем/скрываем пустое состояние
        binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
    
    private fun updateTotalAmount() {
        val total = allExpenses.sumOf { it.amount }
        val numberFormat = DecimalFormat("#,###.00")
        binding.tvTotalExpenses.text = getString(R.string.total_expenses, numberFormat.format(total))
    }
    
    private fun showDeleteDialog(expense: Expense) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_expense)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteExpense(expense)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun deleteExpense(expense: Expense) {
        expense.id?.let { id ->
            binding.progressIndicator.visibility = View.VISIBLE
            
            lifecycleScope.launch {
                try {
                    val response = AppData.apiService.deleteExpense(id)
                    if (response.isSuccessful) {
                        loadExpenses()
                        Toast.makeText(this@MainActivity, "Расход удален", Toast.LENGTH_SHORT).show()
                    } else {
                        showError(getString(R.string.error))
                    }
                } catch (e: Exception) {
                    showError("Ошибка: ${e.message}")
                } finally {
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }
    
    private fun showDateFilterDialog() {
        // Простая реализация фильтра по дате
        // В полной версии можно использовать DatePicker
        Toast.makeText(this, "Фильтр по дате будет реализован", Toast.LENGTH_SHORT).show()
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_statistics -> {
                startActivity(Intent(this, StatisticsActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_refresh -> {
                loadExpenses()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Обновляем список при возврате на экран только если активность уже была создана
        if (::binding.isInitialized) {
            loadExpenses()
        }
    }
}

