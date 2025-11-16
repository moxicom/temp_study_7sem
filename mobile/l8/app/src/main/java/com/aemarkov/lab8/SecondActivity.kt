package com.aemarkov.lab8

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    private lateinit var lifecycleLogTextView: TextView
    private val lifecycleLogBuffer = StringBuilder()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        
        lifecycleLogTextView = findViewById(R.id.tvLifecycleLogs)
        
        // Фиксируем событие onCreate
        appendLifecycleEvent("onCreate()")
        
        // Возврат на главный экран
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    override fun onStart() {
        super.onStart()
        appendLifecycleEvent("onStart()")
    }
    
    override fun onResume() {
        super.onResume()
        appendLifecycleEvent("onResume()")
    }
    
    override fun onPause() {
        super.onPause()
        appendLifecycleEvent("onPause()")
    }
    
    override fun onStop() {
        super.onStop()
        appendLifecycleEvent("onStop()")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        appendLifecycleEvent("onDestroy()")
    }
    
    override fun onRestart() {
        super.onRestart()
        appendLifecycleEvent("onRestart()")
    }
    
    // Добавление записи о событии жизненного цикла
    private fun appendLifecycleEvent(event: String) {
        lifecycleLogBuffer.append("${event}\n")
        lifecycleLogTextView.text = lifecycleLogBuffer.toString()
    }
}