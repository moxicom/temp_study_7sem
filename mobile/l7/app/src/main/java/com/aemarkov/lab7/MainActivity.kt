package com.aemarkov.lab7

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    
    companion object {
		private const val NOTIFICATION_CHANNEL_ID = "notification_channel"
    }

	private var notificationSequenceId = 0
    
    // Запрос разрешения на отправку уведомлений для Android 13+
	private val notificationPermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showNotification()
        } else {
            Toast.makeText(
                this,
                "Разрешение на отправку уведомлений отклонено",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Настройка отступов для edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Создание канала уведомлений для Android 8.0+
        createNotificationChannel()
        
        // Настройка кнопки для отправки уведомления
        findViewById<Button>(R.id.btnSendNotification).setOnClickListener {
            // Проверка разрешения для Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        showNotification()
                    }
                    else -> {
						notificationPermissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                // Для версий Android ниже 13 разрешение не требуется
                showNotification()
            }
        }
    }
    
    // канал уведомлений для Android 8.0+
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channelName = "Основной канал уведомлений"
			val channelDescription = "Канал для основных уведомлений приложения"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance).apply {
				description = channelDescription
            }
            
            // Регистрация канала в системе
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    // уведомления
    private fun showNotification() {
        // Создание Intent для открытия MainActivity при нажатии на уведомление
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        // Создание PendingIntent
		val contentPendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Длинный текст для расширенного стиля
		val notificationExpandedText = "Это расширенный текст уведомления, который будет отображаться " +
                "при развертывании уведомления. Здесь можно разместить более подробную информацию " +
                "о событии или действии, которое требуется от пользователя."
        
        // создание уведомления
		val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentTitle("Тестовое уведомление №$notificationSequenceId")
            .setContentText("Нажмите, чтобы открыть приложение")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
			.setStyle(NotificationCompat.BigTextStyle().bigText(notificationExpandedText))
        
        // отправка уведомления
        with(NotificationManagerCompat.from(this)) {
            try {
				notify(notificationSequenceId++, notificationBuilder.build())
            } catch (e: SecurityException) {
                // Обработка исключения, если разрешение не предоставлено
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка отправки уведомления: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}