package com.example.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WorkshopSettings(
    val workshopName: String = "Atelier de Réparation",
    val workshopPhone: String = "06 00 00 00 00",
    val workshopCity: String = "Casablanca",
    val language: String = "ar", // "ar" or "fr"
    val themeMode: String = "light", // "light" is clean white soft palette
    val defaultStickerSize: String = "50x30mm",
    val autoPrintAfterIntake: Boolean = true,
    val defaultWarrantyDays: Int = 30,
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = "",
    val supabaseAutoSync: Boolean = false,
    val lastSyncTimestamp: String = ""
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("workshop_settings_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<WorkshopSettings> = _settings.asStateFlow()

    private fun loadSettings(): WorkshopSettings {
        return WorkshopSettings(
            workshopName = prefs.getString("workshopName", "Phone Repair Register") ?: "Phone Repair Register",
            workshopPhone = prefs.getString("workshopPhone", "") ?: "",
            workshopCity = prefs.getString("workshopCity", "Maroc") ?: "Maroc",
            language = prefs.getString("language", "ar") ?: "ar",
            themeMode = prefs.getString("themeMode", "light") ?: "light",
            defaultStickerSize = prefs.getString("defaultStickerSize", "50x30mm") ?: "50x30mm",
            autoPrintAfterIntake = prefs.getBoolean("autoPrintAfterIntake", true),
            defaultWarrantyDays = prefs.getInt("defaultWarrantyDays", 30),
            supabaseUrl = prefs.getString("supabaseUrl", "") ?: "",
            supabaseAnonKey = prefs.getString("supabaseAnonKey", "") ?: "",
            supabaseAutoSync = prefs.getBoolean("supabaseAutoSync", false),
            lastSyncTimestamp = prefs.getString("lastSyncTimestamp", "") ?: ""
        )
    }

    fun updateSettings(newSettings: WorkshopSettings) {
        prefs.edit()
            .putString("workshopName", newSettings.workshopName)
            .putString("workshopPhone", newSettings.workshopPhone)
            .putString("workshopCity", newSettings.workshopCity)
            .putString("language", newSettings.language)
            .putString("themeMode", newSettings.themeMode)
            .putString("defaultStickerSize", newSettings.defaultStickerSize)
            .putBoolean("autoPrintAfterIntake", newSettings.autoPrintAfterIntake)
            .putInt("defaultWarrantyDays", newSettings.defaultWarrantyDays)
            .putString("supabaseUrl", newSettings.supabaseUrl)
            .putString("supabaseAnonKey", newSettings.supabaseAnonKey)
            .putBoolean("supabaseAutoSync", newSettings.supabaseAutoSync)
            .putString("lastSyncTimestamp", newSettings.lastSyncTimestamp)
            .apply()
        _settings.value = newSettings
    }

    fun setLanguage(langCode: String) {
        val current = _settings.value
        updateSettings(current.copy(language = langCode))
    }

    fun setThemeMode(mode: String) {
        val current = _settings.value
        updateSettings(current.copy(themeMode = mode))
    }
}
