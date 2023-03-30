package com.akshatbhuhagal.mynotes.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshatbhuhagal.mynotes.R

class PinActivity : AppCompatActivity() {

    private lateinit var pinInput: EditText
    private lateinit var pinSubmit: Button
    private lateinit var pinMessage: TextView
    private var datasender = DataSender()

    private var isSettingPin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        pinInput = findViewById(R.id.pin_input)
        pinSubmit = findViewById(R.id.pin_submit)
        pinMessage = findViewById(R.id.pin_message)
        datasender.obtainAndroidID(this.contentResolver)

        // Check if there is already a passcode
        datasender.checkExists() { valid: Boolean ->
            if (valid) {
                // Pin exists, ask to enter password.
                pinMessage.text = "Enter your password:"

                pinSubmit.setOnClickListener {
                    val password = pinInput.text.toString()
                    datasender.verifyPin(password) { valid: Boolean ->
                        if (valid) {
                            // Pin is valid
                            println("Password correct")
                            startActivity(Intent(this, SplashScreenActivity::class.java))
                        } else {
                            // Pin is invalid
                            runOnUiThread {
                                Toast.makeText(this, "Invalid password.", Toast.LENGTH_SHORT).show()
                            }
                            return@verifyPin
                        }
                    }
                }
            } else {
                // Pin does not exist, start registration process.
                pinMessage.text = "Enter a new password:"

                pinSubmit.setOnClickListener {
                    val password = pinInput.text.toString()
                    if (password.length < 6) {
                        runOnUiThread {
                            Toast.makeText(this, "Password must have at least 6 characters.", Toast.LENGTH_SHORT).show()
                        }
                        return@setOnClickListener
                    }
                    datasender.registerPassword(password) { valid: Boolean ->
                        if (valid) {
                            // Pin is valid
                            startActivity(Intent(this, SplashScreenActivity::class.java))
                        } else {
                            // Error setting new password
                            runOnUiThread {
                                Toast.makeText(this, "Error setting new password, please try again.", Toast.LENGTH_SHORT).show()
                            }
                            return@registerPassword
                        }
                    }
                }

            }


        }
    }
}