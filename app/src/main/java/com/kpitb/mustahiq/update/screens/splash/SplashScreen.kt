package com.kpitb.mustahiq.update.screens.splash

import android.app.Activity
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.navigation.Screen
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: MustahiqViewModel, navController: NavHostController) {

    val isPlaying by remember {
        mutableStateOf(true)
    }

    val speed by remember {
        mutableFloatStateOf(1f)
    }

    val composition by rememberLottieComposition(

        LottieCompositionSpec
            .RawRes(R.raw.load)
    )

    val context = LocalContext.current
    val activity = context as? Activity

    // Initialize InAppUpdate
    LaunchedEffect(activity) {
        activity?.let {
            viewModel.initializeInAppUpdate(it)
            viewModel.checkForUpdates()
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    val progress by animateLottieCompositionAsState(
        composition,

        iterations = LottieConstants.IterateForever,

        isPlaying = isPlaying,

        speed = speed,

        restartOnPlay = false

    )

    val scale = remember { Animatable(1f) } // Scale starts at 1x

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 5f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = { OvershootInterpolator().getInterpolation(it) }
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.zakat_deptt_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp, 140.dp)
                    .padding(bottom = 20.dp)
                    .offset(y = (-80).dp)
            )
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Animated Logo",
                modifier = Modifier
                    .size(50.dp, 40.dp)
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value
                    )
                    .padding(bottom = 10.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.height(100.dp)
            )
        }
    }
}