package com.aemarkov.lab3

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mTextSize = 20f
    private lateinit var mEdit: EditText
    private lateinit var tSize: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mEdit = findViewById(R.id.edit_text)
        tSize = findViewById(R.id.size)

        val buttonB = findViewById<ToggleButton>(R.id.btn_b)
        val buttonI = findViewById<ToggleButton>(R.id.btn_i)
        val buttonSans = findViewById<RadioButton>(R.id.btn_sans)
        val buttonSerif = findViewById<RadioButton>(R.id.btn_serif)
        val buttonMonospace = findViewById<RadioButton>(R.id.btn_monospace)
        val buttonPlus = findViewById<Button>(R.id.button_plus)
        val buttonMinus = findViewById<Button>(R.id.button_minus)

        buttonB.setOnClickListener(this)
        buttonI.setOnClickListener(this)
        buttonSans.setOnClickListener(this)
        buttonSerif.setOnClickListener(this)
        buttonMonospace.setOnClickListener(this)
        buttonPlus.setOnClickListener(this)
        buttonMinus.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.button_plus -> {
                if (mTextSize <= 72) mTextSize += 2f
                mEdit.textSize = mTextSize
                tSize.text = mTextSize.toInt().toString()
            }

            R.id.button_minus -> {
                if (mTextSize >= 20) mTextSize -= 2f
                mEdit.textSize = mTextSize
                tSize.text = mTextSize.toInt().toString()
            }

            R.id.btn_sans -> mEdit.setTypeface(Typeface.SANS_SERIF, mEdit.typeface.style)

            R.id.btn_serif -> mEdit.setTypeface(Typeface.SERIF, mEdit.typeface.style)

            R.id.btn_monospace -> mEdit.setTypeface(Typeface.MONOSPACE, mEdit.typeface.style)

            R.id.btn_b -> {
                if (mEdit.typeface.style == Typeface.ITALIC)
                    mEdit.setTypeface(mEdit.typeface, Typeface.BOLD_ITALIC)
                else if (mEdit.typeface.style == Typeface.BOLD_ITALIC)
                    mEdit.setTypeface(mEdit.typeface, Typeface.ITALIC)
                else if (mEdit.typeface.style == Typeface.BOLD)
                    mEdit.setTypeface(Typeface.create(mEdit.typeface, Typeface.NORMAL))
                else
                    mEdit.setTypeface(mEdit.typeface, Typeface.BOLD)
            }

            R.id.btn_i -> {
                if (mEdit.typeface.style == Typeface.BOLD)
                    mEdit.setTypeface(mEdit.typeface, Typeface.BOLD_ITALIC)
                else if (mEdit.typeface.style == Typeface.BOLD_ITALIC)
                    mEdit.setTypeface(mEdit.typeface, Typeface.BOLD)
                else if (mEdit.typeface.style == Typeface.ITALIC)
                    mEdit.setTypeface(Typeface.create(mEdit.typeface, Typeface.NORMAL))
                else
                    mEdit.setTypeface(mEdit.typeface, Typeface.ITALIC)
            }
        }
    }
}