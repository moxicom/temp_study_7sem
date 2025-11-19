package com.aemarkov.lab2.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aemarkov.lab2.databinding.ActivityAddExpenseBinding
import com.aemarkov.lab2.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddExpenseActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: AddExpenseViewModel by viewModels()
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    private var selectedDate: Date? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupCategoryDropdown()
        setupDatePicker()
        setupButtons()
        observeViewModel()
        
        // Set default date to current date/time
        selectedDate = Date()
        updateDateDisplay()
    }
    
    private fun setupCategoryDropdown() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                val categoryNames = categories.map { it.name }
                val adapter = ArrayAdapter(
                    this@AddExpenseActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    categoryNames
                )
                binding.categoryAutoComplete.setAdapter(adapter)
            }
        }
    }
    
    private fun setupDatePicker() {
        binding.dateEditText.setOnClickListener {
            showDateTimePicker()
        }
    }
    
    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        selectedDate?.let { calendar.time = it }
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedDate = calendar.time
                        updateDateDisplay()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun updateDateDisplay() {
        selectedDate?.let {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            binding.dateEditText.setText(sdf.format(it))
        }
    }
    
    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            finish()
        }
        
        binding.saveButton.setOnClickListener {
            saveExpense()
        }
    }
    
    private fun saveExpense() {
        val amountText = binding.amountEditText.text.toString()
        val category = binding.categoryAutoComplete.text.toString()
        val description = binding.descriptionEditText.text.toString().takeIf { it.isNotBlank() }
        
        if (amountText.isBlank()) {
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (category.isBlank()) {
            Toast.makeText(this, "Выберите категорию", Toast.LENGTH_SHORT).show()
            return
        }
        
        val date = selectedDate?.let {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            sdf.format(it)
        } ?: viewModel.getCurrentDateTime()
        
        viewModel.createExpense(amount, category, description, date)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
                binding.saveButton.isEnabled = !isLoading
            }
        }
        
        lifecycleScope.launch {
            viewModel.isSuccess.collect { expense ->
                expense?.let {
                    notificationHelper.showExpenseAddedNotification(it.category)
                    Toast.makeText(this@AddExpenseActivity, "Расход добавлен", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(this@AddExpenseActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

