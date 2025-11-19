package com.aemarkov.lab9

import android.app.TimePickerDialog
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
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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
        val notificationsEnabled = AppData.prefs.getBoolean(AppData.KEY_NOTIFICATIONS_ENABLED, false)
        binding.switchNotifications.isChecked = notificationsEnabled
        binding.tilNotificationTime.isEnabled = notificationsEnabled
        
        val hour = AppData.prefs.getInt(AppData.KEY_NOTIFICATION_HOUR, 9)
        val minute = AppData.prefs.getInt(AppData.KEY_NOTIFICATION_MINUTE, 0)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        binding.etNotificationTime.setText(timeFormat.format(calendar.time))
        
        // Загружаем лимит бюджета
        val budgetLimit = AppData.prefs.getFloat(AppData.KEY_BUDGET_LIMIT, 0f)
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
        val editor = AppData.prefs.edit()
        
        // Сохраняем настройки уведомлений
        editor.putBoolean(AppData.KEY_NOTIFICATIONS_ENABLED, binding.switchNotifications.isChecked)
        
        val timeText = binding.etNotificationTime.text.toString()
        if (timeText.isNotEmpty()) {
            try {
                val time = timeFormat.parse(timeText)
                if (time != null) {
                    val calendar = Calendar.getInstance().apply {
                        this.time = time
                    }
                    editor.putInt(AppData.KEY_NOTIFICATION_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                    editor.putInt(AppData.KEY_NOTIFICATION_MINUTE, calendar.get(Calendar.MINUTE))
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

