package com.aemarkov.lab4

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var checkBoxShow: CheckBox
    private lateinit var switchMode: Switch
    private lateinit var radioGroup: RadioGroup
    private lateinit var imageView: ImageView
    private lateinit var imageButtonChange: ImageButton
    private lateinit var textDescription: TextView

    private val imagesMap = mapOf(
        R.drawable.man to "Man",
        R.drawable.woman to "Woman",
        R.drawable.cat to "Кошка",
        R.drawable.dog to "Собака",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkBoxShow = findViewById(R.id.checkBoxShow)
        switchMode = findViewById(R.id.switchMode)
        radioGroup = findViewById(R.id.radioGroup)
        imageView = findViewById(R.id.imageView)
        imageButtonChange = findViewById(R.id.imageButtonChange)
        textDescription = findViewById(R.id.textDescription)

        checkBoxShow.setOnCheckedChangeListener { _, isChecked ->
            imageView.visibility = if (isChecked) ImageView.VISIBLE else ImageView.INVISIBLE
            textDescription.text = if (isChecked) "Картинка отображается" else "Картинка скрыта"
        }

        switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setImage(R.drawable.woman)
            } else {
                setImage(R.drawable.man)
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioCat -> setImage(R.drawable.cat)
                R.id.radioDog -> setImage(R.drawable.dog)
            }
        }

        val images = imagesMap.keys.toList()
        imageButtonChange.setOnClickListener {
            val randomImage = images[Random.nextInt(images.size)]
            setImage(randomImage)
        }

        setImage(R.drawable.man)
    }

    private fun setImage(resId: Int) {
        imageView.setImageResource(resId)
        textDescription.text = "Отображается: ${imagesMap[resId]}"
    }
}
