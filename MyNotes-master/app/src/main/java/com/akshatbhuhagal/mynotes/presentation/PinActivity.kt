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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        pinInput = findViewById(R.id.pin_input)
        pinSubmit = findViewById(R.id.pin_submit)
        pinMessage = findViewById(R.id.pin_message)
        datasender.obtainAndroidID(this.contentResolver)

        // For password confirmation
        var confirmed = false
        var initialPassword = ""

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
                if (!confirmed) {
                    pinMessage.text = "Enter a new password:"
                } else {
                    pinMessage.text = "Confirm password:"
                }

                pinSubmit.setOnClickListener {
                    val password = pinInput.text.toString()

                    if (password.isBlank()) {
                        runOnUiThread {
                            Toast.makeText(this, "Password cannot be blank.", Toast.LENGTH_SHORT).show()
                        }
                        return@setOnClickListener
                    }

                    // Check password format
                    val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,64}\$")
                    if (!regex.matches(password)) {
                        runOnUiThread {
                            Toast.makeText(this, "Password must be at least 8 characters long, contain at least one alphabet and one number, and not exceed 64 characters.", Toast.LENGTH_SHORT).show()
                        }
                        return@setOnClickListener
                    }

                    // To trigger confirmation of password
                    if (!confirmed) {
                        confirmed = true
                        initialPassword = password
                        pinInput.setText("")
                        pinMessage.text = "Confirm password:"
                    } else if (initialPassword != password) {
                        runOnUiThread {
                            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                        confirmed = false
                        initialPassword = ""
                        pinInput.setText("")
                        pinMessage.text = "Enter a new password:"
                    } else {
                        datasender.registerPassword(password) { valid: Boolean ->
                            if (valid) {
                                // Password saved to server
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

}