package com.vz.skne.ui.components

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.vz.skne.data.model.Album
import com.vz.skne.data.model.Artist
import com.vz.skne.data.model.Image
import com.vz.skne.data.model.Track
import com.vz.skne.data.repository.SpotifyRepository
import com.vz.skne.network.RetrofitClient
import com.vz.skne.ui.theme.FloatingContainer
import com.vz.skne.ui.theme.桜の雨Theme
import com.vz.skne.ui.theme.AppElevations

@Composable
fun ArtistScreen(
    artistId: String?,
    navController: NavController,
    spotifyRepository: SpotifyRepository,
    modifier: Modifier = Modifier
) {
    var artistData by remember { mutableStateOf<Artist?>(null) }
    var topTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(artistId, spotifyRepository) {
        if (artistId == null) {
            errorMessage = "Artist ID is missing."
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null

        val fetchedArtist = spotifyRepository.getArtist(artistId)
        val fetchedTopTracks = spotifyRepository.getArtistTopTracks(artistId)
        val fetchedAlbums = spotifyRepository.getArtistAlbums(artistId)

        fetchedArtist.onSuccess { artist ->
            artistData = artist
        }.onFailure { e ->
            errorMessage = e.localizedMessage ?: "Unknown error fetching artist details."
            Log.e("ArtistScreen", "Error fetching artist details: ", e)
        }

        fetchedTopTracks.onSuccess { tracks ->
            topTracks = tracks.tracks
        }.onFailure { e ->
            errorMessage = e.localizedMessage ?: "Unknown error fetching top tracks."
            Log.e("ArtistScreen", "Error fetching top tracks: ", e)
        }

        fetchedAlbums.onSuccess { artistAlbums ->
            albums = artistAlbums.albums
        }.onFailure { e ->
            errorMessage = e.localizedMessage ?: "Unknown error fetching albums."
            Log.e("ArtistScreen", "Error fetching albums: ", e)
        }

        isLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text("Error: ${errorMessage}", color = MaterialTheme.colorScheme.error)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Floating Bio Card ---
                var isBioPressed by remember { mutableStateOf(false) }
                val bioElevation by animateDpAsState(
                    targetValue = if (isBioPressed) AppElevations.FloatingDock else AppElevations.SubtleShadow,
                    label = "BioElevationAnimation"
                )
                FloatingContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(top = 16.dp, bottom = 12.dp)
                        .clickable { isBioPressed = !isBioPressed } // Toggle state on click
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = artistData?.images?.firstOrNull()?.url,
                            contentDescription = "Artist Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.Gray),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(artistData?.name ?: "Unknown Artist", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${artistData?.followers?.total?.let { String.format("%,d", it) } ?: "N/A"} followers",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (artistData?.genres?.isNotEmpty() == true) {
                            Text(
                                artistData?.genres?.joinToString(", ") ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // --- Top Tracks Section ---
                var isTopTracksPressed by remember { mutableStateOf(false) }
                val topTracksElevation by animateDpAsState(
                    targetValue = if (isTopTracksPressed) AppElevations.FloatingDock else AppElevations.SubtleShadow,
                    label = "TopTracksElevationAnimation"
                )
                FloatingContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 12.dp)
                        .clickable { isTopTracksPressed = !isTopTracksPressed }
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Text("Top Tracks", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        if (topTracks.isEmpty()) {
                            Text("No top tracks found.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            LazyColumn {
                                items(topTracks, key = { it.id }) { track ->
                                    TrackItem(track = track)
                                }
                            }
                        }
                    }
                }

                // --- Grid/List of Albums ---
                var isAlbumGridPressed by remember { mutableStateOf(false) }
                val albumGridElevation by animateDpAsState(
                    targetValue = if (isAlbumGridPressed) AppElevations.FloatingDock else AppElevations.SubtleShadow,
                    label = "AlbumGridElevationAnimation"
                )
                FloatingContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 12.dp)
                        .clickable { isAlbumGridPressed = !isAlbumGridPressed } // Toggle state on click
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Text("Albums", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        if (albums.isEmpty()) {
                            Text("No albums found.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(albums, key = { it.id }) { album ->
                                    AlbumCard(album = album) {
                                        // TODO: Navigate to album details or play album
                                        // navController.navigate("album_details/${it.id}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                text = track.artists.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AlbumCard(album: Album, onClick: () -> Unit) {
    var isAlbumCardPressed by remember { mutableStateOf(false) }
    val albumCardElevation by animateDpAsState(
        targetValue = if (isAlbumCardPressed) AppElevations.FloatingDock else AppElevations.SubtleShadow,
        label = "AlbumCardElevationAnimation"
    )

    FloatingContainer(
        modifier = Modifier
            .clickable { isAlbumCardPressed = !isAlbumCardPressed; onClick() }
            .aspectRatio(1f),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = albumCardElevation)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = album.images.firstOrNull()?.url,
                contentDescription = album.name,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(album.name, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 2)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistScreenPreview() {
    桜の雨Theme {
        val navController = rememberNavController()
        // Create a dummy repository for the preview
        val dummyRepository = object : SpotifyRepository(RetrofitClient.apiService, RetrofitClient.authService) {
            init { setAccessToken("dummy_token") }
            override suspend fun getArtist(artistId: String): Result<Artist> {
                return Result.success(Artist("id", "Preview Artist", listOf(Image("https://via.placeholder.com/150", 150, 150)), listOf("pop", "rock"), com.vz.skne.data.model.Followers(null, 10000)))
            }
            override suspend fun getArtistTopTracks(artistId: String, market: String): Result<com.vz.skne.data.model.ArtistTopTracks> {
                return Result.success(com.vz.skne.data.model.ArtistTopTracks(emptyList()))
            }
            override suspend fun getArtistAlbums(artistId: String, market: String, limit: Int): Result<com.vz.skne.data.model.ArtistAlbums> {
                return Result.success(com.vz.skne.data.model.ArtistAlbums(listOf(
                    Album("album1", "Preview Album 1", listOf(Image("https://via.placeholder.com/150/FF004F/FFFFFF?text=Album1", 150, 150)), "2023-01-01", 10),
                    Album("album2", "Preview Album 2", listOf(Image("https://via.placeholder.com/150/FF004F/FFFFFF?text=Album2", 150, 150)), "2022-05-15", 8)
                )))
            }
        }
        ArtistScreen(artistId = "previewArtist123", navController = navController, spotifyRepository = dummyRepository)
    }
}
