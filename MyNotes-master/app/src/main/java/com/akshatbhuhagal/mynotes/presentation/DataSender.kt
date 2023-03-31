package com.akshatbhuhagal.mynotes.presentation

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class DataSender : AppCompatActivity(){
    private val client = OkHttpClient()
    private var androidId: String = ""

    fun obtainAndroidID(contentResolver : ContentResolver) {
        this.androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        println(androidId)
    }

    fun sha256(input: String): String {
        val bytes = input.toByteArray(StandardCharsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun generateSalt(): String {
        val random = SecureRandom()
        // 32 byte salt
        val salt = ByteArray(32)
        random.nextBytes(salt)
        return salt.encodeBase64()
    }

    private fun ByteArray.encodeBase64(): String {
        return android.util.Base64.encodeToString(this, android.util.Base64.NO_WRAP)
    }

    fun registerPassword(password: String, callback: (Boolean) -> Unit) {
        val url = "https://www.priceless-elgamal.cloud/registerpassword.php"
        val salt = generateSalt()
        val passwordhash = sha256(salt+password)
        val formBody = FormBody.Builder()
            .add("id", this.androidId)
            .add("passwordhash", passwordhash)
            .add("salt", salt)
            .build()
        val request = Request.Builder().url(url).post(formBody).build()

        // Call the function from a coroutine
        lifecycleScope.launch {
            try {
                handleRequest(request, callback)
            } catch (e: Exception) {
                Log.e("Error:", "Failed to send data.", e)
                callback(false)
            }
        }
    }

    fun verifyPin(password: String, callback: (Boolean) -> Unit) {
        val url = "https://www.priceless-elgamal.cloud/verifypassword.php"
        val formBody = FormBody.Builder()
            .add("id", this.androidId)
            .add("password", password)
            .build()
        val request = Request.Builder().url(url).post(formBody).build()

        // Call the function from a coroutine
        lifecycleScope.launch {
            try {
                handleRequest(request, callback)
            } catch (e: Exception) {
                Log.e("Error:", "Failed to send data.", e)
                callback(false)
            }
        }
    }

    fun checkExists(callback: (Boolean) -> Unit) {
        val url = "https://www.priceless-elgamal.cloud/checkexist.php"
        val formBody = FormBody.Builder()
            .add("id", this.androidId)
            .build()
        val request = Request.Builder().url(url).post(formBody).build()

        // Call the function from a coroutine
        lifecycleScope.launch {
            try {
                handleRequest(request, callback)
            } catch (e: Exception) {
                Log.e("Error:", "Failed to send data.", e)
                callback(false)
            }
        }
    }

    private fun handleRequest(request: Request, callback: (Boolean) -> Unit) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error here by printing it
                println("Error: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the response here
                val body = response.body?.string()?.trim()
                println(body)
                if (body == "true") {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        })
    }

}