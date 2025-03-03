package com.kpitb.mustahiq.update.screens.hospital

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.models.Hospital
import com.kpitb.mustahiq.update.screens.components.ActionButton
import com.kpitb.mustahiq.update.screens.components.ActionButtonForMaps
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.screens.components.CustomAlertDialog
import com.kpitb.mustahiq.update.screens.components.DetailRow
import com.kpitb.mustahiq.update.utils.AnalyticsHelper
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalDetailsScreen(
    navController: NavHostController,
    hospital: Hospital,
    viewModel: MustahiqViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val layoutDirection = when (currentLanguage) {
        "ur", "ps" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    val context = LocalContext.current

    val analyticsHelper = AnalyticsHelper(context)
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenVisit("HospitalDetailsScreen")
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsHelper.logSessionDuration()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.hospital_details),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailRow(
                            label = stringResource(id = R.string.hospital_name),
                            value = when (currentLanguage) {
                                "ur" -> hospital.dh_name_urdu
                                "ps" -> hospital.dh_name_pushto
                                else -> hospital.dh_name
                            }
                        )

                        DetailRow(
                            label = stringResource(id = R.string.focal_person_name),
                            value = when (currentLanguage) {
                                "ur" -> hospital.dh_focal_person_urdu
                                "ps" -> hospital.dh_focal_person_pushto
                                else -> hospital.dh_focal_person
                            }
                        )

                        DetailRow(
                            label = stringResource(id = R.string.focal_person_number),
                            value = hospital.dh_phone
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ActionButton(
                    text = stringResource(id = R.string.contact_now),
                    icon = Icons.Default.Phone,
                    onClick = { showDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ActionButtonForMaps(
                    context = context,
                    text = stringResource(id = R.string.find_office_location),
                    onClick = {
                        openGoogleMaps(
                            context,
                            hospital.dh_latitude.toDouble(),
                            hospital.dh_longitude.toDouble()
                        )
                    }
                )
                val chairmanName = when (currentLanguage) {
                    "ur" -> hospital.dh_focal_person_urdu
                    "ps" -> hospital.dh_focal_person_pushto
                    else -> hospital.dh_focal_person
                }
                CustomAlertDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    title = stringResource(id = R.string.contact_person),
                    message = stringResource(id = R.string.do_you_want_to_contact) +
                            " $chairmanName",
                    confirmButtonText = stringResource(id = R.string.yes),
                    dismissButtonText = stringResource(id = R.string.no),
                    onConfirm = {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:${hospital.dh_phone}")
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

fun openGoogleMaps(context: Context, latitude: Double, longitude: Double) {
    val uri = Uri.parse("geo:0,0?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
    }
}