package com.kpitb.mustahiq.update.screens.districtoffice

import android.content.Context
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.models.District
import com.kpitb.mustahiq.update.navigation.Screen
import com.kpitb.mustahiq.update.screens.components.AnimatedClickable
import com.kpitb.mustahiq.update.utils.AnalyticsHelper
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictOfficesScreen(
    navController: NavHostController,
    viewModel: MustahiqViewModel,
    onBackPressedDispatcher: OnBackPressedDispatcher
) {
    val districtOffices by viewModel.districtOffices.observeAsState(null)
    var searchQuery by remember { mutableStateOf("") }

    val filteredDistrictOffices = remember(searchQuery, districtOffices) {
        districtOffices?.filter { office ->
            val englishName = office.dist_name
            val officeName = when (viewModel.currentLanguage.value) {
                "ur" -> office.dist_name_urdu
                "ps" -> office.dist_name_pashto
                else -> office.dist_name
            }
            officeName.contains(searchQuery, ignoreCase = true) ||
                    englishName.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchDistrictOffices()
    }

    val context = LocalContext.current

    val analyticsHelper = AnalyticsHelper(context)
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenVisit("DistrictOfficesScreen")
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsHelper.logSessionDuration()
        }
    }

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val layoutDirection = when (currentLanguage) {
        "ur", "ps" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.district_offices),
                            color = MaterialTheme.colorScheme.surface
                        )
                    },
                    navigationIcon = {
                        AnimatedClickable(
                            context = context,
                            soundResId = R.raw.click_sound,
                            onClick = { onBackPressedDispatcher.onBackPressed() }
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
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(id = R.string.search_district_offices)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    }
                )
                when {
                    districtOffices == null -> {
                        Text(
                            text = stringResource(id = R.string.loading_district_offices),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    districtOffices!!.isEmpty() -> {
                        Text(
                            text = stringResource(id = R.string.no_district_offices_available),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    else -> {
                        val sortedDistrictOffices = filteredDistrictOffices!!.sortedBy { office ->
                            office.dist_name // Replace `name` with the actual property representing the office's name
                        }

                        LazyColumn {
                            items(sortedDistrictOffices!!) { office ->
                                DistrictOfficeItem(
                                    context,
                                    office,
                                    currentLanguage,
                                    navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DistrictOfficeItem(
    context: Context,
    office: District,
    currentLanguage: String,
    navController: NavHostController
) {
    AnimatedClickable(
        context = context,
        soundResId = R.raw.click_sound,
        onClick = {
            navController.navigate(Screen.DistrictOfficeDetails.createRoute(office.dist_name))
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val officeName = when (currentLanguage) {
                    "ur" -> office.dist_name_urdu
                    "ps" -> office.dist_name_pashto
                    else -> office.dist_name
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.izla_icon),
                        contentDescription = "image",
                        modifier = Modifier
                            .size(20.dp, 20.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = officeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}