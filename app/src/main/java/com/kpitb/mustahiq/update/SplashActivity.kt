package com.kpitb.mustahiq.update

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.kpitb.mustahiq.update.navigation.MyApp
import com.kpitb.mustahiq.update.screens.main.language_and_theme.LanguageNightModePreferences
import com.kpitb.mustahiq.update.ui.theme.ZakatAndUsherTheme
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModelFactory
import java.util.Locale

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val viewModel: MustahiqViewModel by viewModels {
        MustahiqViewModelFactory(applicationContext)
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        // Check if this is the first time the app is opened
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            logAppInstall() // Log install event
            sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
        }

        logAppOpened()

        applySavedPreferences()
        enableEdgeToEdge()
        setContent {
            ZakatAndUsherTheme(darkTheme = viewModel.isNightMode.value) {
                MyApp(
                    viewModel = viewModel,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                )
            }
        }
    }

    private fun logAppInstall() {
        val installer = packageManager.getInstallerPackageName(packageName)
        val installSource = when (installer) {
            "com.android.vending" -> "Google Play Store"
            null -> "Unknown Source (Direct APK)"
            else -> installer
        }

        val bundle = Bundle().apply {
            putString("install_source", installSource)
        }
        firebaseAnalytics.logEvent("app_installed", bundle)
    }

    private fun logAppOpened() {
        val bundle = Bundle().apply {
            putString("app_status", "opened")
        }
        firebaseAnalytics.logEvent("app_opened", bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode)
    }

    private fun applySavedPreferences() {
        val preferences = LanguageNightModePreferences()
        val savedLanguage = preferences.getSavedLanguagePreference(this)
        val isNightMode = preferences.getNightModePreference(this)

        changeAppLanguage(this, savedLanguage)
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun changeAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}