package com.aemarkov.lab2.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.aemarkov.lab2.R

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "expense_tracker_channel"
        private const val CHANNEL_NAME = "Expense Tracker Notifications"
        private const val NOTIFICATION_ID = 1
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о добавлении расходов"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showExpenseAddedNotification(categoryName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Расход добавлен")
            .setContentText("В категорию $categoryName добавлен расход")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

