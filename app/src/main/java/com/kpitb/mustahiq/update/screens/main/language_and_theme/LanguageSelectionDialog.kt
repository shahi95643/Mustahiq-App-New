package com.kpitb.mustahiq.update.screens.main.language_and_theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel

@Composable
fun LanguageSelectionDialog(
    context: Context,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    viewModel: MustahiqViewModel,
) {
    val selectedLanguage by viewModel.currentLanguage.collectAsState()
    var showMessage by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                listOf("English" to "en", "اردو" to "ur", "پشتو" to "ps").forEach { (label, code) ->
                    AnimatedClickable(
                        context = context,
                        soundResId = R.raw.click_sound,
                        onClick = {
                            if (selectedLanguage == code) {
                                showMessage = true
                            } else {
                                onLanguageSelected(code)
                                showMessage = false
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    if (selectedLanguage == code) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selectedLanguage == code) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedLanguage == code) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (showMessage) {
                    Text(
                        text = "Language already selected",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            AnimatedClickable(
                context = context,
                soundResId = R.raw.click_sound,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}