package com.aemarkov.lab2.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aemarkov.lab2.data.models.Category
import com.aemarkov.lab2.data.models.Expense
import com.aemarkov.lab2.data.models.ExpenseRequest
import com.aemarkov.lab2.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isSuccess = MutableStateFlow<Expense?>(null)
    val isSuccess: StateFlow<Expense?> = _isSuccess.asStateFlow()
    
    init {
        loadCategories()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCategories().collect { result ->
                result.onSuccess { categories ->
                    _categories.value = categories
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun createExpense(
        amount: Double,
        category: String,
        description: String?,
        date: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val expenseRequest = ExpenseRequest(
                amount = amount,
                category = category,
                description = description,
                date = date
            )
            
            repository.createExpense(expenseRequest).collect { result ->
                result.onSuccess { expense ->
                    _isSuccess.value = expense
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun resetSuccess() {
        _isSuccess.value = null
    }
}

