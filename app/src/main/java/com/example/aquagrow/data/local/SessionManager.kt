package com.example.aquagrow.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.aquagrow.AquagrowApp
import com.example.aquagrow.data.model.domain.Permission
import com.example.aquagrow.data.model.domain.User
import org.json.JSONObject
import java.util.Date
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object SessionManager {

    private var prefs: SharedPreferences? = null
    private val context: Context get() = AquagrowApp.instance

    private fun ensureInitialized() {
        if (prefs == null) {
            try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                prefs = EncryptedSharedPreferences.create(
                    context,
                    "aquagrow_token_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                prefs = context.getSharedPreferences("emergency_prefs", Context.MODE_PRIVATE)
            }
        }
    }

    fun saveAuthData(token: String, user: User) {
        ensureInitialized()
        prefs?.edit()?.apply {
            putString("auth_token", token)
            putInt("user_id", user.id_usuario)
            putString("user_name", user.usuario)
            putString("user_type", user.tipo_usuario?.nombre)
            // Guardar permisos como JSON
            val permisosJson = Gson().toJson(user.permisos)
            putString("permission", permisosJson)
            // Guarda la fecha como timestamp (long)
            user.fecha_ultima_conexion?.let {
                putLong("last_connection", it.time)
            }
            apply()
        }
    }

    fun getToken(): String? {
        ensureInitialized()
        return prefs?.getString("auth_token", null)
    }

    fun getPermissions(): List<Permission> {
        ensureInitialized()
        return try {
            val permisosJson = prefs?.getString("permission", null)
            if (permisosJson.isNullOrEmpty()) {
                emptyList()
            } else {
                // Crear TypeToken para manejar correctamente la lista genérica
                val listType: Type = object : TypeToken<List<Permission>>() {}.type
                Gson().fromJson(permisosJson, listType) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al obtener permisos", e)
            emptyList()
        }
    }

    fun getUserId(): Int? {
        ensureInitialized()
        return prefs?.getInt("user_id", -1).takeIf { it != -1 }
    }

    fun getUserName(): String? {
        ensureInitialized()
        return prefs?.getString("user_name", null)
    }

    fun getUserType(): String? {
        ensureInitialized()
        return prefs?.getString("user_type", null)
    }

    fun getLastConnection(): Date? {
        ensureInitialized()
        val timestamp = prefs?.getLong("last_connection", -1)
        return if (timestamp != null && timestamp > 0) Date(timestamp) else null
    }

    fun clearAuthData() {
        ensureInitialized()
        prefs?.edit()?.apply {
            remove("auth_token")
            remove("user_id")
            remove("user_name")
            remove("user_type")
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun isTokenExpired(): Boolean {
        ensureInitialized()
        val token = getToken() ?: return true
        val expiration = getTokenExpiration(token)

        return expiration != null && expiration < System.currentTimeMillis() / 1000
    }

    private fun getTokenExpiration(token: String): Long? {
        return try {
            // Los tokens JWT tienen 3 partes separadas por puntos: header.payload.signature
            val parts = token.split(".")
            if (parts.size != 3) return null

            // Decodificar la parte del payload (índice 1) que contiene la información
            val payload = String(
                Base64.decode(parts[1], Base64.URL_SAFE),
                Charsets.UTF_8
            )

            // Parsear el payload JSON para obtener la expiración (exp)
            val jsonObject = JSONObject(payload)
            jsonObject.getLong("exp")
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al decodificar token", e)
            null
        }
    }
}