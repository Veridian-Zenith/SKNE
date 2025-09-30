package com.vz.skne.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp), // Slightly rounded corners
    medium = RoundedCornerShape(8.dp), // More rounded for larger elements
    large = RoundedCornerShape(12.dp) // For prominent floating elements
)

// Define a specific shape for the "floating dock" elements, if needed
val FloatingDockShape = RoundedCornerShape(12.dp)
