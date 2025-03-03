package com.kpitb.mustahiq.update.screens.zakatcommittees

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.models.Tehsil
import com.kpitb.mustahiq.update.screens.components.ActionButton
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.screens.components.CustomAlertDialog
import com.kpitb.mustahiq.update.screens.components.DetailRow
import com.kpitb.mustahiq.update.utils.AnalyticsHelper
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCommitteesScreenDetails(
    navController: NavHostController,
    tehsil: Tehsil,
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
        analyticsHelper.logScreenVisit("ZakatCommitteesScreenDetails")
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsHelper.logSessionDuration()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        val localZakat = when (currentLanguage) {
            "ur" -> tehsil.tehsil_name_urdu
            "ps" -> tehsil.tehsil_name_pashto
            else -> tehsil.tehsil_name
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.local_zakat_committee),
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
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = localZakat,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
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
                            label = stringResource(id = R.string.local_zakat_committee_name),
                            value = when (currentLanguage) {
                                "ur" -> tehsil.lzc_name_urdu
                                "ps" -> tehsil.lzc_name_pashto
                                else -> tehsil.lzc_name
                            }
                        )

                        DetailRow(
                            label = stringResource(id = R.string.tehsil_name),
                            value = when (currentLanguage) {
                                "ur" -> tehsil.tehsil_name_urdu
                                "ps" -> tehsil.tehsil_name_pashto
                                else -> tehsil.tehsil_name
                            }
                        )

                        DetailRow(
                            label = stringResource(id = R.string.chairman_name),
                            value = when (currentLanguage) {
                                "ur" -> tehsil.lzc_chairman_urdu
                                "ps" -> tehsil.lzc_chairman_pashto
                                else -> tehsil.lzc_chairman
                            }
                        )

                        DetailRow(
                            label = stringResource(id = R.string.chairman_number),
                            value = tehsil.lzc_phone
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ActionButton(
                    text = stringResource(id = R.string.contact_chairman),
                    icon = Icons.Default.Phone,
                    onClick = { showDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val chairmanName = when (currentLanguage) {
                    "ur" -> tehsil.lzc_chairman_urdu
                    "ps" -> tehsil.lzc_chairman_pashto
                    else -> tehsil.lzc_chairman
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
                            Uri.parse("tel:${tehsil.lzc_phone}")
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}