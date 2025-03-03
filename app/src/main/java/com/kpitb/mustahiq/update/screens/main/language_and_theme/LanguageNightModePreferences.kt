package com.kpitb.mustahiq.update.screens.main.language_and_theme

import android.content.Context
import android.content.SharedPreferences

class LanguageNightModePreferences {

    companion object {
        private const val PREFERENCES_FILE = "app_preferences"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NIGHT_MODE = "night_mode"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    fun getSavedLanguagePreference(context: Context): String {
        return getPreferences(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun saveLanguagePreference(context: Context, language: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getNightModePreference(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_NIGHT_MODE, false)
    }

    fun saveNightModePreference(context: Context, isNightMode: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_NIGHT_MODE, isNightMode).apply()
    }
}