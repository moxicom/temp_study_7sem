package com.aemarkov.lab9

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aemarkov.lab9.api.ApiClient
import com.aemarkov.lab9.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
        
        loadSettings()
        setupViews()
    }
    
    private fun setupViews() {
        // Кнопка сохранения
        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun loadSettings() {
        // Загружаем лимит бюджета
        val budgetLimit = AppData.prefs.getFloat(AppData.KEY_BUDGET_LIMIT, 0f)
        if (budgetLimit > 0) {
            binding.etBudgetLimit.setText(budgetLimit.toString())
        }
        
        // Загружаем URL API
        val apiUrl = ApiClient.getBaseUrl(this)
        binding.etApiUrl.setText(apiUrl)
    }
    
    private fun saveSettings() {
        val editor = AppData.prefs.edit()
        
        // Сохраняем лимит бюджета
        val budgetLimitText = binding.etBudgetLimit.text.toString()
        if (budgetLimitText.isNotEmpty()) {
            try {
                val budgetLimit = budgetLimitText.toFloat()
                editor.putFloat(AppData.KEY_BUDGET_LIMIT, budgetLimit)
            } catch (e: Exception) {
                // Ошибка парсинга
            }
        }
        
        // Сохраняем URL API
        val apiUrl = binding.etApiUrl.text.toString()
        if (apiUrl.isNotEmpty()) {
            ApiClient.updateBaseUrl(this, apiUrl)
        }
        
        editor.apply()
        
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

