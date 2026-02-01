package com.example.airblock

import android.content.Context
import androidx.core.content.edit

object TagStorage {
    private const val PREFS_NAME = "AirBlockSecurePrefs"
    private const val KEY_TAG_ID = "saved_nfc_tag_id"
    private const val KEY_IS_LOCKED = "is_locked_state"

    private const val LOCKED_TIME = "locked_timestamp"


    // 1. GUARDAR (Escribir en disco)
    fun saveTag(context: Context, tagId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_TAG_ID, tagId) }
    }

    // 2. LEER (Recuperar del disco)
    fun getSavedTag(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TAG_ID, null)
    }

    // 3. BORRAR (Olvidar)
    fun clearTag(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_TAG_ID) }
    }

    fun saveLockState(context: Context, isLocked: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_IS_LOCKED, isLocked) }
    }

    // 👇 NUEVO: RECUPERAR ESTADO DE BLOQUEO
    fun getLockState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Por defecto devolvemos 'false' (Desbloqueado) si no sabe nada
        return prefs.getBoolean(KEY_IS_LOCKED, false)
    }

    fun saveStartTime(context: Context, timestamp: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putLong(LOCKED_TIME, timestamp) }
    }

    // 👇 RECUPERAR HORA OBJETIVO
    fun getStartTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Si no hay dato, devolvemos 0
        return prefs.getLong(LOCKED_TIME, 0L)
    }
}