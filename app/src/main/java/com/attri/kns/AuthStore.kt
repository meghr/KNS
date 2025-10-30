package com.attri.kns

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setCredentials(username: String, password: String) {
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }
}
