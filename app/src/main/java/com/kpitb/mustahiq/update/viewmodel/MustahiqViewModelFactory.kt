package com.kpitb.mustahiq.update.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MustahiqViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MustahiqViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MustahiqViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}