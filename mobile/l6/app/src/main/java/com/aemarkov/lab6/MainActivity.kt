package com.aemarkov.lab6

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), MyDialogFragment.MyDialogFragmentListener {

    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.textViewResult)
        val buttonOpenDialog = findViewById<Button>(R.id.buttonOpenDialog)

        buttonOpenDialog.setOnClickListener {
            val dialogFragment = MyDialogFragment()
            dialogFragment.show(supportFragmentManager, "MyDialogFragment")
        }
    }

    override fun onDialogSendClicked(text: String) {
        textViewResult.text = text
    }

    override fun onDialogExitClicked() {
        finish()
    }
}