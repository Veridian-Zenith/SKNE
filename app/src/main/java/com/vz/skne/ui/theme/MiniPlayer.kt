package com.vz.skne.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MiniPlayer(
    trackName: String,
    artistName: String,
    albumArt: @Composable () -> Unit, // Allows passing an Image composable or similar
    onPlayPauseClick: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    FloatingContainer(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Album Art and Track Info on the left
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp)) { // Smaller size for mini player art
                    albumArt()
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = trackName, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    Text(text = artistName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }

            // Play/Pause button on the right
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary, // Use primary color for emphasis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MiniPlayerPreview() {
    桜の雨Theme { // Ensure theme is applied for preview
        MiniPlayer(
            trackName = "Song Title Here",
            artistName = "Artist Name",
            albumArt = { /* Placeholder for Album Art */ },
            onPlayPauseClick = { /* TODO */ },
            isPlaying = false,
        )
    }
}
