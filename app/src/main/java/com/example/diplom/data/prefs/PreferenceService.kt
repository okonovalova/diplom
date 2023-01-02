package com.example.diplom.data.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject

class PreferenceService @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val PREFERENCE_NAME_COMMON = "common_pref"
        private const val PREFERENCE_NAME_CRYPTO = "crypto_pref"
    }

    private val preference by lazy {
        context.getSharedPreferences(PREFERENCE_NAME_COMMON, Context.MODE_PRIVATE)
    }

    private val masterKey by lazy {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    private val crypto by lazy {
        EncryptedSharedPreferences.create(
            PREFERENCE_NAME_CRYPTO,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var accessToken: String?
        get() = crypto.getString(::accessToken.name, null)
        set(value) {
            crypto.edit().putString(::accessToken.name, value).apply()
        }

    var userId: Int
        get() = crypto.getInt(::userId.name, 0)
        set(value) {
            crypto.edit().putInt(::userId.name, value).apply()
        }

    fun clear() {
        preference.edit().clear().apply()
        crypto.edit().clear().apply()
    }
}