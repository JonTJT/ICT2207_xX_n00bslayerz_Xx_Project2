package com.akshatbhuhagal.mynotes.presentation

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSharedPreferences(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "MyAppPreferences",
        MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePassword(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("password", password)
        editor.apply()
    }

    fun getSavedPassword(): String {
        return sharedPreferences.getString("password", "") ?: ""
    }
}