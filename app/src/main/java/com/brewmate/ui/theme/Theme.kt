package com.brewmate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BrewColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    secondary = Caramel,
    background = WarmCream,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    tertiary = LatteAccent
)

@Composable
fun BrewMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrewColorScheme,
        typography = BrewTypography,
        content = content
    )
}