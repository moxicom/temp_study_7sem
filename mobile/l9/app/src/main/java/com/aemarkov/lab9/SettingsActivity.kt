package com.aemarkov.lab9

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aemarkov.lab9.api.ApiClient
import com.aemarkov.lab9.databinding.ActivitySettingsBinding
import com.aemarkov.lab9.service.NotificationService
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    
    private val PREFS_NAME = "app_prefs"
    private val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private val KEY_NOTIFICATION_HOUR = "notification_hour"
    private val KEY_NOTIFICATION_MINUTE = "notification_minute"
    private val KEY_BUDGET_LIMIT = "budget_limit"
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
        
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        loadSettings()
        setupViews()
    }
    
    private fun setupViews() {
        // Переключатель уведомлений
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            binding.tilNotificationTime.isEnabled = isChecked
            if (isChecked) {
                scheduleNotification()
            } else {
                NotificationService.cancelDailyNotification(this)
            }
        }
        
        // Выбор времени уведомления
        binding.etNotificationTime.setOnClickListener {
            showTimePicker()
        }
        
        // Кнопка сохранения
        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun loadSettings() {
        // Загружаем настройки уведомлений
        val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
        binding.switchNotifications.isChecked = notificationsEnabled
        binding.tilNotificationTime.isEnabled = notificationsEnabled
        
        val hour = prefs.getInt(KEY_NOTIFICATION_HOUR, 9)
        val minute = prefs.getInt(KEY_NOTIFICATION_MINUTE, 0)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        binding.etNotificationTime.setText(timeFormat.format(calendar.time))
        
        // Загружаем лимит бюджета
        val budgetLimit = prefs.getFloat(KEY_BUDGET_LIMIT, 0f)
        if (budgetLimit > 0) {
            binding.etBudgetLimit.setText(budgetLimit.toString())
        }
        
        // Загружаем URL API
        val apiUrl = ApiClient.getBaseUrl(this)
        binding.etApiUrl.setText(apiUrl)
    }
    
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timeText = binding.etNotificationTime.text.toString()
        
        if (timeText.isNotEmpty()) {
            try {
                val time = timeFormat.parse(timeText)
                if (time != null) {
                    calendar.time = time
                }
            } catch (e: Exception) {
                // Используем текущее время
            }
        }
        
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                binding.etNotificationTime.setText(timeFormat.format(calendar.time))
                scheduleNotification()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    private fun scheduleNotification() {
        val timeText = binding.etNotificationTime.text.toString()
        if (timeText.isNotEmpty()) {
            try {
                val time = timeFormat.parse(timeText)
                if (time != null) {
                    val calendar = Calendar.getInstance().apply {
                        this.time = time
                    }
                    NotificationService.scheduleDailyNotification(
                        this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE)
                    )
                }
            } catch (e: Exception) {
                // Ошибка парсинга времени
            }
        }
    }
    
    private fun saveSettings() {
        val editor = prefs.edit()
        
        // Сохраняем настройки уведомлений
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, binding.switchNotifications.isChecked)
        
        val timeText = binding.etNotificationTime.text.toString()
        if (timeText.isNotEmpty()) {
            try {
                val time = timeFormat.parse(timeText)
                if (time != null) {
                    val calendar = Calendar.getInstance().apply {
                        this.time = time
                    }
                    editor.putInt(KEY_NOTIFICATION_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                    editor.putInt(KEY_NOTIFICATION_MINUTE, calendar.get(Calendar.MINUTE))
                }
            } catch (e: Exception) {
                // Ошибка парсинга времени
            }
        }
        
        // Сохраняем лимит бюджета
        val budgetLimitText = binding.etBudgetLimit.text.toString()
        if (budgetLimitText.isNotEmpty()) {
            try {
                val budgetLimit = budgetLimitText.toFloat()
                editor.putFloat(KEY_BUDGET_LIMIT, budgetLimit)
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
        
        // Планируем уведомления, если они включены
        if (binding.switchNotifications.isChecked) {
            scheduleNotification()
        } else {
            NotificationService.cancelDailyNotification(this)
        }
        
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

