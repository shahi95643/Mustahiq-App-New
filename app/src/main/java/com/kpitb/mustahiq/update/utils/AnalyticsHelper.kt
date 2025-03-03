package com.kpitb.mustahiq.update.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsHelper(context: Context) {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private var sessionStartTime: Long = 0

    init {
        sessionStartTime = System.currentTimeMillis() // Track when the session starts
    }

    // Log screen visit
    fun logScreenVisit(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Log.d("Analytics", "Screen visited: $screenName")
    }

    // Log form downloads
    fun logFormDownload(formName: String) {
        val bundle = Bundle().apply {
            putString("form_name", formName)
            putString("action", "download")
        }
        firebaseAnalytics.logEvent("form_download", bundle)
        Log.d("Analytics", "Form downloaded: $formName")
    }

    // Log session duration when the app is closed
    @SuppressLint("DefaultLocale")
    fun logSessionDuration() {
        val sessionEndTime = System.currentTimeMillis()
        val sessionDurationMillis = sessionEndTime - sessionStartTime

        // Convert milliseconds to hours, minutes, and seconds
        val seconds = (sessionDurationMillis / 1000) % 60
        val minutes = (sessionDurationMillis / (1000 * 60)) % 60
        val hours = (sessionDurationMillis / (1000 * 60 * 60)) % 24

        val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        val bundle = Bundle().apply {
            putString("session_duration", formattedDuration)
        }
        firebaseAnalytics.logEvent("session_duration", bundle)
        Log.d("Analytics", "Session duration: $formattedDuration (hh:mm:ss)")
    }
}
