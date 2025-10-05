package com.vz.skne.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vz.skne.AppRoutes
import com.vz.skne.ui.theme.AppElevations
import com.vz.skne.ui.theme.FloatingContainer
import com.vz.skne.ui.theme.MiniPlayer
import com.vz.skne.ui.theme.桜の雨Theme

@Composable
fun HomeScreen(
    trackName: String,
    artistName: String,
    artistId: String?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    albumArtComposable: @Composable () -> Unit,
    lyricsContent: String,
    navController: NavController,
    isConnecting: Boolean = false,
    connectionError: String? = null,
    onRetryConnection: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            // Connection status
            if (isConnecting) {
                ConnectionStatusCard(
                    message = "Connecting to Spotify...",
                    isError = false,
                )
            } else if (connectionError != null) {
                ConnectionStatusCard(
                    message = connectionError,
                    isError = true,
                    onRetry = onRetryConnection,
                )
            }

            AlbumArtDisplay(
                trackName = trackName,
                artistName = artistName,
                artistId = artistId,
                albumArtComposable = albumArtComposable,
                navController = navController,
            )
            Spacer(modifier = Modifier.height(16.dp))
            LyricsPanel(lyricsContent = lyricsContent)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(AppRoutes.LIKED_SONGS_SCREEN) }) {
                Icon(Icons.Default.Favorite, contentDescription = "Liked Songs", modifier = Modifier.padding(end = 8.dp))
                Text("View Liked Songs")
            }
        }
        PlaybackControls(
            isPlaying = isPlaying,
            onPlayPauseClick = onPlayPauseClick,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
        )
        MiniPlayer(
            trackName = trackName,
            artistName = artistName,
            albumArt = albumArtComposable,
            onPlayPauseClick = onPlayPauseClick,
            isPlaying = isPlaying,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )
    }
}

@Composable
private fun ConnectionStatusCard(
    message: String,
    isError: Boolean,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    FloatingContainer(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isError) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )

            if (isError && onRetry != null) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onRetry) {
                    Text("Retry", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun AlbumArtDisplay(
    trackName: String,
    artistName: String,
    artistId: String?,
    albumArtComposable: @Composable () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    FloatingContainer(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(top = 16.dp, bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            albumArtComposable()
            Spacer(modifier = Modifier.height(8.dp))
            Text(trackName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = artistName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    artistId?.let { id ->
                        navController.navigate("${AppRoutes.ARTIST_SCREEN}/$id")
                    }
                },
            )
        }
    }
}

@Composable
private fun LyricsPanel(lyricsContent: String) {
    val scrollState = rememberScrollState()
    var isLyricsPressed by remember { mutableStateOf(false) }
    val lyricsElevation by animateDpAsState(
        targetValue = if (isLyricsPressed) AppElevations.FloatingDock else AppElevations.SubtleShadow,
        label = "LyricsElevationAnimation",
    )

    FloatingContainer(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 16.dp, bottom = 12.dp)
            .clickable { isLyricsPressed = !isLyricsPressed },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                Text(lyricsContent, style = MaterialTheme.typography.bodyLarge)
            }

            if (scrollState.canScrollForward || scrollState.canScrollBackward) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingContainer(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            var isPrevPressed by remember { mutableStateOf(false) }
            val prevScale by animateFloatAsState(
                targetValue = if (isPrevPressed) 0.9f else 1.0f,
                animationSpec = tween(durationMillis = 100),
                label = "PrevScaleAnimation",
            )
            IconButton(
                onClick = {
                    onPreviousClick()
                    isPrevPressed = true
                },
                modifier = Modifier.graphicsLayer(scaleX = prevScale, scaleY = prevScale),
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous Track",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            LaunchedEffect(isPrevPressed) {
                if (isPrevPressed) {
                    kotlinx.coroutines.delay(100)
                    isPrevPressed = false
                }
            }
            var isPlayPausePressed by remember { mutableStateOf(false) }
            val playPauseScale by animateFloatAsState(
                targetValue = if (isPlayPausePressed) 0.9f else 1.0f,
                animationSpec = tween(durationMillis = 100),
                label = "PlayPauseScaleAnimation",
            )
            IconButton(
                onClick = {
                    onPlayPauseClick()
                    isPlayPausePressed = true
                },
                modifier = Modifier.graphicsLayer(
                    scaleX = playPauseScale,
                    scaleY = playPauseScale,
                ),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            LaunchedEffect(isPlayPausePressed) {
                if (isPlayPausePressed) {
                    kotlinx.coroutines.delay(100)
                    isPlayPausePressed = false
                }
            }
            var isNextPressed by remember { mutableStateOf(false) }
            val nextScale by animateFloatAsState(
                targetValue = if (isNextPressed) 0.9f else 1.0f,
                animationSpec = tween(durationMillis = 100),
                label = "NextScaleAnimation",
            )
            IconButton(
                onClick = {
                    onNextClick()
                    isNextPressed = true
                },
                modifier = Modifier.graphicsLayer(scaleX = nextScale, scaleY = nextScale),
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next Track",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            LaunchedEffect(isNextPressed) {
                if (isNextPressed) {
                    kotlinx.coroutines.delay(100)
                    isNextPressed = false
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    桜の雨Theme {
        HomeScreen(
            trackName = "Currently Playing Song",
            artistName = "Artist Name",
            artistId = "artist123",
            isPlaying = false,
            onPlayPauseClick = { },
            onNextClick = { },
            onPreviousClick = { },
            albumArtComposable = {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(Color.Gray),
                ) { Text("Art", modifier = Modifier.align(Alignment.Center)) }
            },
            lyricsContent = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
            """.trimIndent(),
            navController = rememberNavController(),
        )
    }
}
