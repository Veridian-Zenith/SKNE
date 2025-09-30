package com.vz.skne.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Import our custom colors
import com.vz.skne.ui.theme.BackgroundDark
import com.vz.skne.ui.theme.BackgroundLight
import com.vz.skne.ui.theme.OnBackgroundDark
import com.vz.skne.ui.theme.OnBackgroundLight
import com.vz.skne.ui.theme.OnPrimaryDark
import com.vz.skne.ui.theme.OnPrimaryLight
import com.vz.skne.ui.theme.OnSecondaryDark
import com.vz.skne.ui.theme.OnSecondaryLight
import com.vz.skne.ui.theme.OnSurfaceDark
import com.vz.skne.ui.theme.OnSurfaceLight
import com.vz.skne.ui.theme.PrimaryDark
import com.vz.skne.ui.theme.PrimaryLight
import com.vz.skne.ui.theme.SecondaryDark
import com.vz.skne.ui.theme.SecondaryLight
import com.vz.skne.ui.theme.SpotifyBlack
import com.vz.skne.ui.theme.SurfaceDark
import com.vz.skne.ui.theme.SurfaceLight

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = SpotifyFuchsia,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onTertiary = SpotifyBlack,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = SpotifyFuchsia,
    onError = SpotifyBlack,
    outline = RosewaterDark,
    surfaceVariant = ElevatedBlack,
    onSurfaceVariant = Rosewater
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = SpotifyFuchsia,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onTertiary = SpotifyBlack,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    error = SpotifyFuchsia,
    onError = Rosewater,
    outline = SpotifyBlack,
    surfaceVariant = RosewaterLight,
    onSurfaceVariant = SpotifyBlack
)

// Elevation values for the floating docks
object AppElevations {
    val FloatingDock = 12.dp // Significant elevation for a floating feel
    val FloatingElement = 8.dp
    val SubtleShadow = 4.dp
}

@Composable
fun 桜の雨Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to ensure consistency with your custom theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Removed dynamic color logic as we're enforcing a custom dark theme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Define custom shapes here to be used within the theme
    val customShapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined elsewhere in this package
        shapes = customShapes, // Apply custom shapes
        content = content
    )
}

// Reusable composable for floating dock-like elements
@Composable
fun FloatingContainer(content: @Composable () -> Unit) {
    ElevatedCard(
        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(
            defaultElevation = AppElevations.FloatingDock
        ),
        shape = FloatingDockShape, // Use the specific floating dock shape
        colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface, // Use surface color for the dock
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        content = { content() }
    )
}

// Placeholder for Typography - ensure this is defined in another file or here
// @Composable
// fun Typography() = ...

// Placeholder for FloatingDockShape if not defined in Shapes.kt
// val FloatingDockShape = RoundedCornerShape(12.dp)
