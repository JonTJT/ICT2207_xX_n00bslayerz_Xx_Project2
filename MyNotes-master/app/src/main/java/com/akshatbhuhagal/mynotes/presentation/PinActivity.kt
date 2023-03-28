package com.akshatbhuhagal.mynotes.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshatbhuhagal.mynotes.R
import org.mindrot.jbcrypt.BCrypt

class PinActivity : AppCompatActivity() {

    private lateinit var pinInput: EditText
    private lateinit var pinSubmit: Button
    private lateinit var pinMessage: TextView

    private var isSettingPin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        pinInput = findViewById(R.id.pin_input)
        pinSubmit = findViewById(R.id.pin_submit)
        pinMessage = findViewById(R.id.pin_message)

        val secureSharedPreferences = SecureSharedPreferences(this)

        if (secureSharedPreferences.getSavedPassword().isEmpty()) {
            pinMessage.text = "Enter a new password:"
        } else {
            pinMessage.text = "Enter your password:"
        }

        pinSubmit.setOnClickListener {
            val password = pinInput.text.toString()
            if (secureSharedPreferences.getSavedPassword().isEmpty()) {
                if (password.length >= 8) {
                    val salt = BCrypt.gensalt()
                    val hashedPassword = BCrypt.hashpw(password, salt)
                    secureSharedPreferences.savePassword(hashedPassword)
                    Toast.makeText(this, "Password set successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                val savedPassword = secureSharedPreferences.getSavedPassword()
                if (BCrypt.checkpw(password, savedPassword)) {
                    setResult(RESULT_OK)
                    Toast.makeText(this, "Password is correct!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Password is incorrect!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            startActivity(Intent(this, SplashScreenActivity::class.java))
        }
    }
}