package com.kpitb.mustahiq.update.ui.theme.color

import androidx.compose.ui.graphics.Color
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors

object ColorPalettes {
    val LightColorPalette = lightColors(
        surface = ThemeColor.WHITE_SMOKE,
        onSurface = Color.Black,

        background = Color.White,
        onBackground = Color.Black,

        secondary = ThemeColor.LIGHT_PASTEL_PURPLE,
        onSecondary = Color.White,

        primary = ThemeColor.PASTEL_PURPLE,
        onPrimary = Color.White,
        primaryVariant = ThemeColor.PASTEL_PURPLE,
        error = ThemeColor.ERROR,
        onError = Color.White
    )

    val DarkColorPalette = darkColors(
        surface = Color.Black,
        onSurface = ThemeColor.WHITE_SMOKE,

        background = ThemeColor.CUSTOM_BLACK50,
        onBackground = ThemeColor.WHITE_SMOKE,

        secondary = ThemeColor.CUSTOM_BLACK80,
        onSecondary = ThemeColor.WHITE_SMOKE,

        primary = ThemeColor.LIGHT_GRAY,
        onPrimary = ThemeColor.CUSTOM_BLACK,
        primaryVariant = ThemeColor.LIGHT_GRAY,
        error = ThemeColor.ERROR,
        onError = Color.White
    )
}