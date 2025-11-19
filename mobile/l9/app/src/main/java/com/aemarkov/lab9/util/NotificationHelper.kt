package com.aemarkov.lab9.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aemarkov.lab9.MainActivity
import com.aemarkov.lab9.R

object NotificationHelper {
    private const val CHANNEL_ID = "expense_channel"
    private const val CHANNEL_NAME = "Уведомления о расходах"
    private const val CHANNEL_DESCRIPTION = "Уведомления о добавлении новых расходов"
    private var notificationId = 0
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showExpenseAddedNotification(context: Context, category: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationText = "Добавлен новый расход в категорию '$category'"
        
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId++, notificationBuilder.build())
            }
        } catch (e: SecurityException) {
            // Игнорируем ошибку, если разрешение не предоставлено
        }
    }
}

