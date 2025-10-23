package com.aemarkov.lab5

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myLayout = findViewById<LinearLayout>(R.id.root)
        return when (item.itemId) {
            R.id.red -> {
                myLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                true
            }
            R.id.green -> {
                myLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                true
            }
            R.id.blue -> {
                myLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                true
            }
            R.id.exit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}