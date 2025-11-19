package com.aemarkov.lab2.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aemarkov.lab2.data.models.Expense
import com.aemarkov.lab2.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val filteredExpenses: StateFlow<List<Expense>> = MutableStateFlow(emptyList())
    
    init {
        loadExpenses()
        setupFilteredExpenses()
    }
    
    private fun setupFilteredExpenses() {
        viewModelScope.launch {
            combine(
                _expenses,
                _searchQuery
            ) { expenses, query ->
                if (query.isBlank()) {
                    expenses
                } else {
                    expenses.filter { expense ->
                        expense.description?.contains(query, ignoreCase = true) == true ||
                        expense.category.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filtered ->
                (filteredExpenses as MutableStateFlow).value = filtered
            }
        }
    }
    
    fun loadExpenses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getExpenses().collect { result ->
                result.onSuccess { expenses ->
                    _expenses.value = expenses
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

