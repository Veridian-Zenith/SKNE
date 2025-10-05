package com.vz.skne

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.vz.skne.data.repository.SpotifyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(val spotifyRepository: SpotifyRepository) : ViewModel() {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var connectionAttempts = 0
    private val maxConnectionAttempts = 3

    // Player state
    private val _playerState = MutableStateFlow<PlayerState?>(null)
    val playerState = _playerState.asStateFlow()

    // Track information
    private val _currentTrackName = MutableStateFlow("No Track Playing")
    val currentTrackName = _currentTrackName.asStateFlow()

    private val _currentArtistName = MutableStateFlow("Unknown Artist")
    val currentArtistName = _currentArtistName.asStateFlow()

    private val _currentArtistId = MutableStateFlow<String?>(null)
    val currentArtistId = _currentArtistId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentAlbumArtUri = MutableStateFlow<String?>(null)
    val currentAlbumArtUri = _currentAlbumArtUri.asStateFlow()

    // Authentication state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError = _connectionError.asStateFlow()

    // Liked songs
    private val _likedSongs = MutableStateFlow<List<String>>(emptyList())
    val likedSongs = _likedSongs.asStateFlow()

    private val _isLoadingLikedSongs = MutableStateFlow(false)
    val isLoadingLikedSongs = _isLoadingLikedSongs.asStateFlow()

    private val _likedSongsError = MutableStateFlow<String?>(null)
    val likedSongsError = _likedSongsError.asStateFlow()

    fun onLoginResult(token: String) {
        _isLoggedIn.value = true
        _connectionError.value = null
        spotifyRepository.setAuthToken(token)
        fetchLikedSongs()
    }

    fun retryConnection(context: Context) {
        if (connectionAttempts < maxConnectionAttempts) {
            connectionAttempts++
            _isConnecting.value = true
            _connectionError.value = null
            connectToSpotify(context)
        } else {
            _connectionError.value = "Max connection attempts reached. Please check your Spotify app and try again."
        }
    }

    fun refreshLikedSongs() {
        fetchLikedSongs()
    }

    private fun fetchLikedSongs() {
        if (_isLoadingLikedSongs.value) return // Prevent multiple simultaneous requests

        _isLoadingLikedSongs.value = true
        _likedSongsError.value = null

        viewModelScope.launch {
            try {
                val result = spotifyRepository.getLikedSongs()
                result.onSuccess { songs ->
                    _likedSongs.value = songs
                    _likedSongsError.value = null
                }.onFailure { exception ->
                    _likedSongsError.value = exception.localizedMessage ?: "Failed to fetch liked songs"
                    Log.e("MainViewModel", "Failed to fetch liked songs", exception)
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Coroutine was cancelled, this is normal
                Log.d("MainViewModel", "Liked songs fetch was cancelled", e)
            } catch (e: Exception) {
                _likedSongsError.value = e.localizedMessage ?: "Failed to fetch liked songs"
                Log.e("MainViewModel", "Failed to fetch liked songs", e)
            } finally {
                _isLoadingLikedSongs.value = false
            }
        }
    }

    fun connectToSpotify(context: Context) {
        if (_isConnecting.value) return // Prevent multiple simultaneous connections

        _isConnecting.value = true
        _connectionError.value = null

        val clientId = BuildConfig.SPOTIFY_CLIENT_ID
        val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI

        // Configure connection params to block telemetry
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(
            context,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(remote: SpotifyAppRemote) {
                    spotifyAppRemote = remote
                    connectionAttempts = 0 // Reset connection attempts on success
                    _isConnecting.value = false
                    _connectionError.value = null
                    Log.d("MainViewModel", "Connected to Spotify successfully!")

                    // Subscribe to player state with error handling
                    spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
                        try {
                            _playerState.value = playerState
                            _currentTrackName.value = playerState.track?.name ?: "No Track Playing"
                            _currentArtistName.value = playerState.track?.artist?.name ?: "Unknown Artist"
                            _currentArtistId.value = playerState.track?.artist?.uri?.substringAfterLast(":") ?: ""
                            _isPlaying.value = !playerState.isPaused
                            _currentAlbumArtUri.value = playerState.track?.imageUri?.raw
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            // Coroutine was cancelled, this is normal
                            Log.d("MainViewModel", "Player state update was cancelled", e)
                        } catch (e: Exception) {
                            Log.e("MainViewModel", "Error updating player state", e)
                        }
                    }?.setErrorCallback { error ->
                        Log.e("MainViewModel", "Player state subscription error", error)
                        _connectionError.value = "Player state error: ${error.message}"
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    _isConnecting.value = false
                    val errorMessage = when {
                        throwable.message?.contains("authentication failed", true) == true ->
                            "Authentication failed. Please check your Spotify app permissions."
                        throwable.message?.contains("no spotify app", true) == true ->
                            "Spotify app not found. Please install the Spotify app."
                        throwable.message?.contains("network", true) == true ->
                            "Network error. Please check your internet connection."
                        else -> "Connection failed: ${throwable.message}"
                    }

                    _connectionError.value = errorMessage
                    Log.e("MainViewModel", "Failed to connect to Spotify", throwable)

                    // Auto-retry logic for certain errors
                    if (connectionAttempts < maxConnectionAttempts &&
                        throwable.message?.contains("network", true) == true
                    ) {
                        viewModelScope.launch {
                            delay(2000L) // Wait 2 seconds before retry
                            retryConnection(context)
                        }
                    }
                }
            },
        )
    }

    fun onPlayPauseClick() {
        spotifyAppRemote?.let { remote ->
            remote.playerApi.playerState.setResultCallback { playerState ->
                if (playerState.isPaused) {
                    remote.playerApi.resume()
                } else {
                    remote.playerApi.pause()
                }
            }
        }
    }

    fun onNextClick() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun onPreviousClick() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    override fun onCleared() {
        super.onCleared()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}
