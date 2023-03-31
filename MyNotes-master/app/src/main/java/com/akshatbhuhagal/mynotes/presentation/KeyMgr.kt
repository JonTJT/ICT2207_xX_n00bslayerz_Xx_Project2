package com.akshatbhuhagal.mynotes.presentation

import android.content.Context
import android.os.Build
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.security.AlgorithmParameters
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class KeyMgr {

    companion object {
        var rawByteKey: ByteArray? = null
        var dbCharKey: CharArray? = null
    }

    fun getCharKey(passcode: CharArray, context: Context): CharArray {
        if (dbCharKey == null) {
            initKey(passcode, context)
        }
        return dbCharKey ?: error("Failed to decrypt database key")
    }

    private fun initKey(passcode: CharArray, context: Context) {
        val storable = getStorable(context)
        if (storable == null) {
            // Create new 256bit RawKey
            createNewKey()
            // Encrypt the RawKey;
            // AES-SecretKey(RawKey), where SecretKey=Password+Salt(PBDKF2)
            persistRawKey(context, rawByteKey, passcode)
        } else {
            // Retrieve the (iv, AES-SecretKey(RawKey), salt)
            rawByteKey = getRawByteKey(passcode, storable)
            dbCharKey = rawByteKey.toHex()
        }
    }

    private fun createNewKey() {
        // This is the raw key that we'll be encrypting + storing
        rawByteKey = generateRandomKey()
        // This is the key that will be used by Room
        dbCharKey = rawByteKey.toHex()
    }

    private fun generateRandomKey(): ByteArray =
        ByteArray(32).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SecureRandom.getInstanceStrong().nextBytes(this)
            } else {
                SecureRandom().nextBytes(this)
            }
        }

    private fun ByteArray?.toHex(): CharArray {
        val result = StringBuilder()
        this?.forEach {
            val HEX_CHARS = "0123456789ABCDEF".toCharArray()
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }
        return result.toString().toCharArray()
    }

    data class Storable(val iv: String, val key: String, val salt: String)

    private fun persistRawKey(context: Context, rawKey: ByteArray?, userPasscode: CharArray) {
        val storable = toStorable(rawKey, userPasscode)
        // Implementation explained in next step
        saveToPrefs(context, storable)
    }

    private fun toStorable(rawDbKey: ByteArray?, userPasscode: CharArray): Storable {
        // Generate a random 8 byte salt
        val salt = ByteArray(8).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SecureRandom.getInstanceStrong().nextBytes(this)
            } else {
                SecureRandom().nextBytes(this)
            }
        }
        val secret: SecretKey = generateSecretKey(userPasscode, salt)

        // Now encrypt the database key with PBE
        val cipher: Cipher = Cipher.getInstance("AES/CTR/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secret)
        val params: AlgorithmParameters = cipher.parameters
        val iv: ByteArray = params.getParameterSpec(IvParameterSpec::class.java).iv
        val ciphertext: ByteArray = cipher.doFinal(rawDbKey)

        // Return the IV and CipherText which can be stored to disk
        return Storable(
            Base64.encodeToString(iv, Base64.DEFAULT),
            Base64.encodeToString(ciphertext, Base64.DEFAULT),
            Base64.encodeToString(salt, Base64.DEFAULT)
        )
    }

    private fun generateSecretKey(passcode: CharArray, salt: ByteArray): SecretKey {
        // Initialize PBE with password
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(passcode, salt, 65536, 256)
        val tmp: SecretKey = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun saveToPrefs(context: Context, storable: Storable) {
        val serialized = Gson().toJson(storable)
        val prefs = context.getSharedPreferences("database",
            Context.MODE_PRIVATE)
        prefs.edit().putString("key", serialized).apply()
    }

    private fun getStorable(context: Context): Storable? {
        val prefs = context.getSharedPreferences("database",
            Context.MODE_PRIVATE)
        val serialized = prefs.getString("key", null)
        if (serialized.isNullOrBlank()) {
            return null
        }
        return try {
            Gson().fromJson(serialized,
                object: TypeToken<Storable>() {}.type)
        } catch (ex: JsonSyntaxException) {
            null
        }
    }

    private fun getRawByteKey(passcode: CharArray, storable: Storable): ByteArray {
        val aesWrappedKey = Base64.decode(storable.key, Base64.DEFAULT)
        val iv = Base64.decode(storable.iv, Base64.DEFAULT)
        println("Debug_Stored IV: " + iv.test())
        val salt = Base64.decode(storable.salt, Base64.DEFAULT)
        println("Debug_Stored Salt: " + salt.test())
        val secret: SecretKey = generateSecretKey(passcode, salt)
        println("Debug_Secret Key generated for decrypting DB_AES Key: " + secret.encoded.test())
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(iv))
        println("Debug_Decrypted DB RawKey: "+ cipher.doFinal(aesWrappedKey).test())
        return cipher.doFinal(aesWrappedKey)
    }

}

fun ByteArray.test(): String {
    return joinToString("") { "%02x".format(it) }
}
fun charArrayToHexString(charArray: CharArray): String {
    return charArray.joinToString(separator = "") {
        String.format("%02x", it.toInt() and 0xff)
    }
}

