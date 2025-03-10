package com.kpitb.mustahiq.update.screens.scheme

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.constants.StringConstants
import com.kpitb.mustahiq.update.models.Scheme
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.screens.components.CardComponentWithTTS
import com.kpitb.mustahiq.update.screens.components.TitleAndDescriptionCardWithTTS
import com.kpitb.mustahiq.update.utils.AnalyticsHelper
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeDetailsScreen(
    navController: NavHostController,
    scheme: Scheme,
    viewModel: MustahiqViewModel
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val layoutDirection = when (currentLanguage) {
        "ur", "ps" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val isTTSEnabled = remember { mutableStateOf(false) }

    val analyticsHelper = AnalyticsHelper(context)
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenVisit("SchemeDetailsScreen")
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsHelper.logSessionDuration()
        }
    }

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = when (currentLanguage) {
                    "ur", "ps" -> {
                        tts.value?.setLanguage(Locale("ur")) // Use Urdu locale
                        tts.value?.setSpeechRate(0.9f) // Adjust speed for Urdu
                        tts.value?.setPitch(1.0f) // Natural pitch for Urdu
                    }
                    else -> {
                        tts.value?.setLanguage(Locale.ENGLISH) // Use English locale
                        tts.value?.setSpeechRate(0.8f) // Adjust speed for English
                        tts.value?.setPitch(0.9f) // Slightly lower pitch for a softer tone
                    }
                }

                Log.d("TTS", "Language result: $result")
                isTTSEnabled.value = result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                        result == TextToSpeech.LANG_AVAILABLE

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TTS", "TTS language missing or not supported. Prompting user to install.")
                    val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                    context.startActivity(installIntent)
                }
            } else {
                Log.e("TTS", "Initialization failed")
                isTTSEnabled.value = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.zakat_schemes),
                            color = MaterialTheme.colorScheme.surface
                        )
                    },
                    navigationIcon = {
                        AnimatedClickable(
                            context = context,
                            soundResId = R.raw.click_sound,
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                val title = when (currentLanguage) {
                    "ur" -> scheme.scheme_title_urdu
                    "ps" -> scheme.scheme_title_urdu
                    else -> scheme.scheme_title
                }
                val description = when (currentLanguage) {
                    "ur" -> scheme.scheme_description_urdu
                    "ps" -> scheme.scheme_description_urdu
                    else -> scheme.scheme_description
                }
                val eligibilityCriteria = when (currentLanguage) {
                    "ur" -> scheme.scheme_eligibility_criteria_urdu
                    "ps" -> scheme.scheme_eligibility_criteria_urdu
                    else -> scheme.scheme_eligibility_criteria
                }
                val instructions = when (currentLanguage) {
                    "ur" -> scheme.scheme_apply_instruction_urdu
                    "ps" -> scheme.scheme_apply_instruction_urdu
                    else -> scheme.scheme_apply_instruction
                }
                var isLoading by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        isLoading = true // Show loading state
                        CoroutineScope(Dispatchers.Main).launch {
                            analyticsHelper.logFormDownload(scheme.scheme_title)
                            delay(300) // Small delay to ensure logging happens before intent execution
                            isLoading = false // Hide loading state
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(StringConstants.fileLink + scheme.file_path))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Download Form", fontSize = 16.sp)
                    }
                }
                TitleAndDescriptionCardWithTTS(
                    title = title,
                    description = description,
                    onSpeak = {
                        val textToSpeak = "$title. $description"
                        tts.value?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    isTTSEnabled = isTTSEnabled.value
                )
                CardComponentWithTTS(
                    title = stringResource(id = R.string.eligibility_criteria),
                    content = eligibilityCriteria,
                    onSpeak = {
                        tts.value?.speak(eligibilityCriteria, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    isTTSEnabled = isTTSEnabled.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                CardComponentWithTTS(
                    title = stringResource(id = R.string.how_to_apply),
                    content = instructions,
                    onSpeak = {
                        tts.value?.speak(instructions, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    isTTSEnabled = isTTSEnabled.value
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading = true // Show loading state
                        CoroutineScope(Dispatchers.Main).launch {
                            analyticsHelper.logFormDownload(scheme.scheme_title)
                            delay(300) // Ensures analytics logs before opening the document
                            isLoading = false // Hide loading state
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(StringConstants.fileLink + scheme.file_path))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Download Form", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}