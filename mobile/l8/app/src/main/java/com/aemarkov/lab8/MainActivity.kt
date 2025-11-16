package com.aemarkov.lab8

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var contactsTextView: TextView
    
    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 100
        private const val CONTACTS_PERMISSION_REQUEST_CODE = 101
        private const val PHONE_URI = "tel:+79001234567" // Демонстрационный номер
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        contactsTextView = findViewById(R.id.tvContacts)
        
        // Переход на экран с журналом жизненного цикла
        findViewById<Button>(R.id.btnOpenSecondActivity).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        
        // Запрос на совершение звонка
        findViewById<Button>(R.id.btnMakeCall).setOnClickListener {
            requestPhoneCall()
        }
        
        // Запрос на чтение контактов
        findViewById<Button>(R.id.btnReadContacts).setOnClickListener {
            requestContactsRead()
        }
    }
    
    // Проверка разрешений и запуск звонка
    private fun requestPhoneCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            launchCall()
        }
    }
    
    // Проверка разрешений и чтение контактов
    private fun requestContactsRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            renderContacts()
        }
    }
    
    // Загрузка и отображение контактов
    private fun renderContacts() {
        val contactsList = StringBuilder()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                val name = if (nameIndex != -1) it.getString(nameIndex) else getString(R.string.unknown_contact)
                val number = if (numberIndex != -1) it.getString(numberIndex) else getString(R.string.no_phone_number)
                
                contactsList.append("$name — $number\n")
            }
        }
        
        if (contactsList.isEmpty()) {
            contactsTextView.text = getString(R.string.contacts_not_found)
        } else {
            contactsTextView.text = contactsList.toString()
        }
    }
    
    // Непосредственный запуск звонка
    private fun launchCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse(PHONE_URI)
        startActivity(intent)
    }
    
    // Обработка результатов запроса разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            CALL_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCall()
                } else {
                    Toast.makeText(this, getString(R.string.call_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    renderContacts()
                } else {
                    Toast.makeText(this, getString(R.string.contacts_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}