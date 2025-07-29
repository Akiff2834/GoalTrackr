// app/src/main/java/com/goaltrackr/ui/theme/Theme.kt
package com.goaltrackr.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Koyu tema renk şeması
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = OnPrimaryColor,
    onSecondary = OnSecondaryColor,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    error = ErrorColor,
    onError = OnErrorColor
)

// Açık tema renk şeması
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimaryColor,
    onSecondary = OnSecondaryColor,
    onBackground = OnBackgroundColor,
    onSurface = OnSurfaceColor,
    error = ErrorColor,
    onError = OnErrorColor
)

@Composable
fun GoalTrackrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Sistem temasını kullanır
    // Dinamik renkler Android 12+ üzerinde kullanılabilir
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Kullanılacak renk şemasını belirler
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // Koyu tema seçiliyse
        else -> LightColorScheme // Açık tema seçiliyse
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // MaterialTheme'i uygular
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Type.kt'den gelen tipografi
        content = content
    )
}
