package com.kpitb.mustahiq.update.screens.components

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun AnimatedClickable(
    context: Context,
    soundResId: Int? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = spring(),
        label = ""
    )

    fun playClickSound() {
        soundResId?.let {
            val mediaPlayer = MediaPlayer.create(context, it)
            mediaPlayer.setOnCompletionListener { player ->
                player.release()
            }
            mediaPlayer.start()
        }
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                playClickSound()
                onClick()
            }
    ) {
        content()
    }
}
