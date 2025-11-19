package com.aemarkov.lab2.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aemarkov.lab2.data.models.Category
import com.aemarkov.lab2.data.models.Statistics
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
class StatisticsViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _statistics = MutableStateFlow<Statistics?>(null)
    val statistics: StateFlow<Statistics?> = _statistics.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow<PeriodFilter>(PeriodFilter.MONTH)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadCategories()
        loadStatistics()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { result ->
                result.onSuccess { categories ->
                    _categories.value = categories
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            }
        }
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val (startDate, endDate) = getDateRange(_selectedPeriod.value)
            val period = when (_selectedPeriod.value) {
                PeriodFilter.DAY -> "day"
                PeriodFilter.WEEK -> "week"
                PeriodFilter.MONTH -> "month"
                PeriodFilter.YEAR -> "year"
            }
            
            repository.getStatistics(
                period = period,
                startDate = startDate,
                endDate = endDate
            ).collect { result ->
                result.onSuccess { stats ->
                    var filteredStats = stats
                    
                    // Apply category filter if selected
                    _selectedCategory.value?.let { category ->
                        filteredStats = stats.copy(
                            byCategory = stats.byCategory.filter { it.category == category },
                            totalAmount = stats.byCategory
                                .filter { it.category == category }
                                .sumOf { it.amount },
                            expenseCount = stats.byCategory
                                .filter { it.category == category }
                                .sumOf { it.count }
                        )
                    }
                    
                    _statistics.value = filteredStats
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
        loadStatistics()
    }
    
    fun setPeriodFilter(period: PeriodFilter) {
        _selectedPeriod.value = period
        loadStatistics()
    }
    
    private fun getDateRange(period: PeriodFilter): Pair<String?, String?> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        
        val endDate = sdf.format(now)
        val startDate = when (period) {
            PeriodFilter.DAY -> {
                calendar.add(Calendar.HOUR_OF_DAY, -24)
                sdf.format(calendar.time)
            }
            PeriodFilter.WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                sdf.format(calendar.time)
            }
            PeriodFilter.MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                sdf.format(calendar.time)
            }
            PeriodFilter.YEAR -> {
                calendar.add(Calendar.YEAR, -1)
                sdf.format(calendar.time)
            }
        }
        
        return Pair(startDate, endDate)
    }
    
    enum class PeriodFilter {
        DAY, WEEK, MONTH, YEAR
    }
}

