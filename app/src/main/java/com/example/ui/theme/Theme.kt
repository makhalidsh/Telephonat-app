package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CleanSoftLightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = BrandBlueLight,
    onPrimaryContainer = BrandBlue,

    secondary = EmeraldSoft,
    onSecondary = Color.White,
    secondaryContainer = EmeraldSoftBg,
    onSecondaryContainer = EmeraldSoft,

    tertiary = AmberSoft,
    onTertiary = Color.White,
    tertiaryContainer = AmberSoftBg,
    onTertiaryContainer = AmberSoft,

    background = CanvasBg,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondary,

    outline = BorderSoft,
    outlineVariant = BorderLight,

    error = RoseSoft,
    onError = Color.White,
    errorContainer = RoseSoftBg,
    onErrorContainer = RoseSoft
)

private val DarkWorkbenchColorScheme = darkColorScheme(
    primary = Amber400,
    onPrimary = Zinc950,
    primaryContainer = AmberDark,
    onPrimaryContainer = Amber400,

    secondary = Emerald400,
    onSecondary = Zinc950,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = Emerald400,

    tertiary = Sky400,
    onTertiary = Zinc950,
    tertiaryContainer = SkyDark,
    onTertiaryContainer = Sky400,

    background = Zinc950,
    onBackground = Zinc100,

    surface = Zinc900,
    onSurface = Zinc100,
    surfaceVariant = Zinc850,
    onSurfaceVariant = Zinc300,

    outline = Zinc700,
    outlineVariant = Zinc800,

    error = Rose400,
    onError = Zinc950,
    errorContainer = RoseDark,
    onErrorContainer = Rose400
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkWorkbenchColorScheme else CleanSoftLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = WorkshopTypography,
        content = content
    )
}
