package com.aemarkov.lab9

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aemarkov.lab9.api.ApiClient
import com.aemarkov.lab9.api.ApiService
import com.aemarkov.lab9.databinding.ActivityAddEditExpenseBinding
import com.aemarkov.lab9.model.ExpenseRequest
import com.aemarkov.lab9.util.DateUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.*

class AddEditExpenseActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddEditExpenseBinding
    private lateinit var apiService: ApiService
    
    private var expenseId: Int? = null
    private var isEditMode = false
    
    private val categories = listOf(
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
        binding = ActivityAddEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        apiService = ApiClient.getApiService(this)
        
        expenseId = intent.getIntExtra("expense_id", -1).takeIf { it != -1 }
        isEditMode = expenseId != null
        
        if (isEditMode) {
            supportActionBar?.title = getString(R.string.edit_expense_title)
            loadExpense()
        } else {
            supportActionBar?.title = getString(R.string.add_expense_title)
        }
        
        setupViews()
    }
    
    private fun setupViews() {
        // Настройка Spinner категорий
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter
        
        // Настройка DatePicker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        
        // Установка текущей даты по умолчанию
        if (!isEditMode) {
            binding.etDate.setText(DateUtils.formatForDisplay(Date()))
        }
        
        // Кнопка сохранения
        binding.btnSave.setOnClickListener {
            saveExpense()
        }
        
        // Кнопка отмены
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val date = binding.etDate.text.toString()
        
        // Пытаемся распарсить текущую дату
        val currentDate = if (date.isNotEmpty()) {
            DateUtils.parseFromIso(date) ?: Date()
        } else {
            Date()
        }
        
        calendar.time = currentDate
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.etDate.setText(DateUtils.formatForDisplay(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun loadExpense() {
        expenseId?.let { id ->
            binding.progressIndicator.visibility = View.VISIBLE
            
            lifecycleScope.launch {
                try {
                    val response = apiService.getExpense(id)
                    if (response.isSuccessful) {
                        val expense = response.body()
                        expense?.let {
                            binding.etAmount.setText(it.amount.toString())
                            binding.etDescription.setText(it.description ?: "")
                            binding.etDate.setText(DateUtils.formatForDisplay(it.date))
                            
                            val categoryIndex = categories.indexOf(it.category)
                            if (categoryIndex >= 0) {
                                binding.spinnerCategory.setSelection(categoryIndex)
                            }
                        }
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
    
    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString()
        val amount = amountText.toDoubleOrNull()
        
        if (amount == null || amount <= 0) {
            binding.etAmount.error = getString(R.string.invalid_amount)
            return
        }
        
        val category = binding.spinnerCategory.selectedItem as? String
        if (category == null) {
            Toast.makeText(this, R.string.invalid_category, Toast.LENGTH_SHORT).show()
            return
        }
        
        val description = binding.etDescription.text.toString().takeIf { it.isNotEmpty() }
        val dateText = binding.etDate.text.toString()
        
        // Преобразуем дату в ISO формат
        // dateText в формате dd.MM.yyyy, нужно распарсить
        val date = try {
            val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            dateFormat.parse(dateText) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        val isoDate = DateUtils.formatToIso(date)
        
        val expenseRequest = ExpenseRequest(
            amount = amount,
            category = category,
            description = description,
            date = isoDate
        )
        
        binding.progressIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = if (isEditMode && expenseId != null) {
                    apiService.updateExpense(expenseId!!, expenseRequest)
                } else {
                    apiService.createExpense(expenseRequest)
                }
                
                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditExpenseActivity, R.string.save_success, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError(getString(R.string.save_error))
                }
            } catch (e: Exception) {
                showError("Ошибка: ${e.message}")
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

