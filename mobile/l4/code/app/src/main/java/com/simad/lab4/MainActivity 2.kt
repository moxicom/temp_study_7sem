//package com.simad.lab4
//
//import android.graphics.Color
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.google.android.material.chip.Chip
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var cbBackground: CheckBox
//    private lateinit var tbTheme: ToggleButton
//    private lateinit var rgAnimal: RadioGroup
//    private lateinit var rbCat: RadioButton
//    private lateinit var rbDog: RadioButton
//    private lateinit var rbBird: RadioButton
//    private lateinit var chipAction: Chip
//    private lateinit var ivAnimal: ImageView
//    private lateinit var ibRefresh: ImageButton
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        initViews()
//        setupListeners()
//        updateImage()
//    }
//
//    private fun initViews() {
//        cbBackground = findViewById(R.id.cbBackground)
//        tbTheme = findViewById(R.id.tbTheme)
//        rgAnimal = findViewById(R.id.rgAnimal)
//        rbCat = findViewById(R.id.rbCat)
//        rbDog = findViewById(R.id.rbDog)
//        rbBird = findViewById(R.id.rbBird)
//        chipAction = findViewById(R.id.chipAction)
//        ivAnimal = findViewById(R.id.ivAnimal)
//        ibRefresh = findViewById(R.id.ibRefresh)
//    }
//
//    private fun setupListeners() {
//        // Обработчик для CheckBox
//        cbBackground.setOnCheckedChangeListener { _, isChecked ->
//            updateImage()
//        }
//
//        // Обработчик для ToggleButton
//        tbTheme.setOnCheckedChangeListener { _, isChecked ->
//            changeTheme(isChecked)
//        }
//
//        // Обработчик для RadioGroup
//        rgAnimal.setOnCheckedChangeListener { _, checkedId ->
//            updateImage()
//        }
//
//        // Обработчик для Chip
//        chipAction.setOnCheckedChangeListener { _, isChecked ->
//            updateImage()
//        }
//
//        // Обработчик для ImageButton
//        ibRefresh.setOnClickListener {
//            resetSelections()
//        }
//    }
//
//    private fun updateImage() {
//        val animalResId = getAnimalImageResource()
//        val actionResId = getActionImageResource()
//
//        // Комбинируем основное изображение и действие
//        val finalResId = if (chipAction.isChecked) actionResId else animalResId
//
//        ivAnimal.setImageResource(finalResId)
//
//        // Устанавливаем фон в зависимости от CheckBox
//        if (cbBackground.isChecked) {
//            ivAnimal.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
//        } else {
//            ivAnimal.setBackgroundColor(Color.TRANSPARENT)
//        }
//    }
//
//    private fun getAnimalImageResource(): Int {
//        return when (rgAnimal.checkedRadioButtonId) {
//            R.id.rbCat -> R.drawable.cat_normal
//            R.id.rbDog -> R.drawable.dog_normal
//            R.id.rbBird -> R.drawable.bird_normal
//            else -> R.drawable.cat_normal
//        }
//    }
//
//    private fun getActionImageResource(): Int {
//        return when (rgAnimal.checkedRadioButtonId) {
//            R.id.rbCat -> R.drawable.cat_action
//            R.id.rbDog -> R.drawable.dog_action
//            R.id.rbBird -> R.drawable.bird_action
//            else -> R.drawable.cat_action
//        }
//    }
//
//    private fun changeTheme(isDark: Boolean) {
//        val backgroundColor = if (isDark) {
//            Color.DKGRAY
//        } else {
//            Color.WHITE
//        }
//
//        val textColor = if (isDark) {
//            Color.WHITE
//        } else {
//            Color.BLACK
//        }
//
//        // Меняем фон основного layout
//        findViewById<LinearLayout>(R.id.root).setBackgroundColor(backgroundColor)
//
//        // Меняем цвет текста у элементов
//        arrayOf(cbBackground, rbCat, rbDog, rbBird).forEach { view ->
//            (view as? TextView)?.setTextColor(textColor)
//        }
//    }
//
//    private fun resetSelections() {
//        cbBackground.isChecked = false
//        tbTheme.isChecked = false
//        rbCat.isChecked = true
//        chipAction.isChecked = false
//        changeTheme(false)
//        updateImage()
//
//        Toast.makeText(this, "Настройки сброшены", Toast.LENGTH_SHORT).show()
//    }
//}