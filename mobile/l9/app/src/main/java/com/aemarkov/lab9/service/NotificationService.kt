package com.aemarkov.lab9.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.aemarkov.lab9.MainActivity
import com.aemarkov.lab9.R
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Создаем канал уведомлений
        createNotificationChannel(notificationManager)
        
        // Создаем уведомление
        val notification = createNotification(applicationContext)
        
        // Показываем уведомление
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        return Result.success()
    }
    
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(context: Context): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Не забудьте вести учет расходов сегодня!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }
    
    companion object {
        private const val CHANNEL_ID = "expense_tracker_channel"
        private const val CHANNEL_NAME = "Учет расходов"
        private const val CHANNEL_DESCRIPTION = "Уведомления о необходимости вести учет расходов"
        private const val NOTIFICATION_ID = 1
    }
}

object NotificationService {
    private const val WORK_NAME = "daily_notification_work"
    
    fun scheduleDailyNotification(context: Context, hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(context)
        
        // Отменяем предыдущую задачу
        workManager.cancelUniqueWork(WORK_NAME)
        
        // Вычисляем время до следующего уведомления
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // Если время уже прошло сегодня, планируем на завтра
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        
        // Планируем периодическую задачу
        val periodicRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicRequest
        )
    }
    
    fun cancelDailyNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_NAME)
    }
    
    fun showBudgetExceededNotification(context: Context, currentAmount: Double, limit: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Создаем канал уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_channel",
                "Превышение бюджета",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, "budget_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Превышен лимит бюджета!")
            .setContentText("Текущие расходы: ${String.format("%.2f", currentAmount)} ₽ из ${String.format("%.2f", limit)} ₽")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(2, notification)
    }
}

