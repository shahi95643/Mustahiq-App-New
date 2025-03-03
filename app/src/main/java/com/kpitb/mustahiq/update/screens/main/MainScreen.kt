package com.kpitb.mustahiq.update.screens.main

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.navigation.Screen
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.screens.components.EnhancedCard
import com.kpitb.mustahiq.update.screens.main.language_and_theme.LanguageSelectionDialog
import com.kpitb.mustahiq.update.ui.theme.color.ThemeColor
import com.kpitb.mustahiq.update.utils.AnalyticsHelper
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel
import kotlinx.coroutines.delay
import java.util.Locale

fun changeAppLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MustahiqViewModel,
    navController: NavHostController,
    onBackPressedDispatcher: OnBackPressedDispatcher,
) {
    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val analyticsHelper = AnalyticsHelper(context)
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenVisit("MainScreen")
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsHelper.logSessionDuration()
        }
    }

    val showIntroDialog = viewModel.shouldShowIntroDialog.collectAsState()
    var selectedLanguage by remember { mutableStateOf("English") }

    if (showIntroDialog.value) {
        IntroDialog(
            onDismiss = { viewModel.setShowIntroDialog(false) },
            onNeverAskAgain = { viewModel.setShowIntroDialog(false, true) },
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { language ->
                selectedLanguage = language
            }
        )
    }

    if (backPressedOnce) {
        LaunchedEffect(Unit) {
            delay(2000L)
            backPressedOnce = false
        }
    }

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showDialog by remember { mutableStateOf(false)}
    val isNightMode by viewModel.isNightMode.collectAsState()

    val nightModeIcon = if (isNightMode) {
        painterResource(id = R.drawable.ic_light)
    } else {
        painterResource(id = R.drawable.ic_night)
    }
    val layoutDirection = when (currentLanguage) {
        "ur", "ps" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    BackHandler {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = MaterialTheme.colorScheme.surface
                        )
                    },
                    actions = {
                        AnimatedClickable(
                            context = context,
                            soundResId = R.raw.click_sound,
                            onClick = { showDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.language_icon),
                                    contentDescription = "Change Language",
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        AnimatedClickable(
                            context = context,
                            soundResId = R.raw.click_sound,
                            onClick = { viewModel.toggleNightMode() }
                        ) {
                            Icon(
                                painter = nightModeIcon,
                                modifier = Modifier
                                    .size(28.dp),
                                contentDescription = "Night Mode",
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            LaunchedEffect(currentLanguage) {
                changeAppLanguage(context, currentLanguage)
            }
            if (showDialog) {
                LanguageSelectionDialog(
                    onLanguageSelected = { languageCode ->
                        viewModel.updateLanguage(languageCode)
                        showDialog = false
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onDismiss = { showDialog = false },
                    viewModel = viewModel,
                    context = context
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        start = 32.dp,
                        top = 32.dp,
                        end = 32.dp,
                        bottom = 16.dp
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EnhancedCard(
                    context = LocalContext.current,
                    navController = navController,
                    cardBackgroundColor = if (isNightMode) MaterialTheme.colorScheme.surface else ThemeColor.LIGHT_PINK,
                    imageTintColor = if (isNightMode) MaterialTheme.colorScheme.onSurface else ThemeColor.PRIMARY_PINK,
                    imageResId = R.drawable.zakat_schem_icon,
                    text = stringResource(id = R.string.zakat_schemes),
                    route = Screen.Scheme.route
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Card 2: Hospitals
                EnhancedCard(
                    context = LocalContext.current,
                    navController = navController,
                    cardBackgroundColor = if (isNightMode) MaterialTheme.colorScheme.surface else ThemeColor.LIGHT_BLUE,
                    imageTintColor = if (isNightMode) MaterialTheme.colorScheme.onSurface else ThemeColor.DARK_BLUE,
                    imageResId = R.drawable.hspital_icon,
                    text = stringResource(id = R.string.pro_hospitals),
                    route = Screen.Hospital.route
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Card 3: District Offices
                EnhancedCard(
                    context = LocalContext.current,
                    navController = navController,
                    cardBackgroundColor = if (isNightMode) MaterialTheme.colorScheme.surface else ThemeColor.LIGHT_YELLOW,
                    imageTintColor = if (isNightMode) MaterialTheme.colorScheme.onSurface else ThemeColor.DARK_YELLOW,
                    imageResId = R.drawable.izla_icon,
                    text = stringResource(id = R.string.distict_offices),
                    route = Screen.DistrictOffice.route
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Card 4: Zakat Scheme Search
                EnhancedCard(
                    context = LocalContext.current,
                    navController = navController,
                    cardBackgroundColor = if (isNightMode) MaterialTheme.colorScheme.surface else ThemeColor.LIGHT_PASTEL_PURPLE,
                    imageTintColor = if (isNightMode) MaterialTheme.colorScheme.onSurface else ThemeColor.PASTEL_PURPLE,
                    imageResId = R.drawable.ic_search,
                    text = stringResource(id = R.string.zakat_search_portal),
                    route = Screen.Search.route
                )
            }
        }
    }
}

@Composable
fun IntroDialog(
    onDismiss: () -> Unit,
    onNeverAskAgain: () -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val textToDisplay = when (selectedLanguage) {
        "Urdu" -> stringResource(id = R.string.dialog_text_urdu)
        else -> stringResource(id = R.string.dialog_text_english)
    }

    val layoutDirection = if (selectedLanguage == "Urdu") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    val englishText = stringResource(id = R.string.dialogtextenglish)
    val urduText = stringResource(id = R.string.dialogtexturdu)

    val intro = if (selectedLanguage == "Urdu") urduText else englishText

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        AlertDialog(
            onDismissRequest = { /* Do nothing or handle if needed */ },
            title = {
                Text(
                    text = intro,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                )  {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Button(
                            onClick = { onLanguageSelected("English") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == "English") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (selectedLanguage == "English") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (selectedLanguage == "English") {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            }
                        ) {
                            Text(stringResource(id = R.string.english))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onLanguageSelected("Urdu") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == "Urdu") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (selectedLanguage == "Urdu") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (selectedLanguage == "Urdu") {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            }
                        ) {
                            Text(stringResource(id = R.string.urdu))
                        }
                    }

                    // Scrollable Text Section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = textToDisplay,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = if (selectedLanguage == "Urdu") TextAlign.Right else TextAlign.Left
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = onNeverAskAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Do Not Show Again")
                }
            }
        )
    }
}
