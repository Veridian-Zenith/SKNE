package com.vz.skne

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.vz.skne.ui.components.HomeScreen
import com.vz.skne.ui.components.ArtistScreen
import com.vz.skne.ui.components.LikedSongsScreen
import com.vz.skne.ui.components.LoginScreen
import com.vz.skne.ui.theme.桜の雨Theme
import com.vz.skne.data.repository.SpotifyRepository
import com.vz.skne.network.RetrofitClient

// Navigation Routes
object AppRoutes {
    const val LOGIN_SCREEN = "login_screen"
    const val HOME_SCREEN = "home_screen"
    const val LIKED_SONGS_SCREEN = "liked_songs_screen"
    const val ARTIST_SCREEN = "artist_screen"
    const val ARTIST_ID_ARG = "artistId"
}

private const val SPOTIFY_AUTH_REQUEST_CODE = 1337

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SpotifyRepository(RetrofitClient.apiService, RetrofitClient.authService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            桜の雨Theme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

                val startDestination = if (isLoggedIn) AppRoutes.HOME_SCREEN else AppRoutes.LOGIN_SCREEN

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppRoutes.LOGIN_SCREEN) {
                            LoginScreen(
                                onLoginClick = {
                                    val request = getAuthorizationRequest()
                                    AuthorizationClient.openLoginActivity(
                                        this@MainActivity,
                                        SPOTIFY_AUTH_REQUEST_CODE,
                                        request
                                    )
                                }
                            )
                        }
                        composable(AppRoutes.HOME_SCREEN) {
                            val currentTrackName by viewModel.currentTrackName.collectAsStateWithLifecycle()
                            val currentArtistName by viewModel.currentArtistName.collectAsStateWithLifecycle()
                            val currentArtistId by viewModel.currentArtistId.collectAsStateWithLifecycle()
                            val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                            val currentAlbumArtUri by viewModel.currentAlbumArtUri.collectAsStateWithLifecycle()
                            val isConnecting by viewModel.isConnecting.collectAsStateWithLifecycle()
                            val connectionError by viewModel.connectionError.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                viewModel.connectToSpotify(context)
                            }

                            HomeScreen(
                                trackName = currentTrackName,
                                artistName = currentArtistName,
                                artistId = currentArtistId,
                                isPlaying = isPlaying,
                                onPlayPauseClick = viewModel::onPlayPauseClick,
                                onNextClick = viewModel::onNextClick,
                                onPreviousClick = viewModel::onPreviousClick,
                                albumArtComposable = {
                                    AlbumArt(uri = currentAlbumArtUri)
                                },
                                lyricsContent = "No lyrics available.", // Placeholder
                                navController = navController,
                                isConnecting = isConnecting,
                                connectionError = connectionError,
                                onRetryConnection = { viewModel.retryConnection(context) }
                            )
                        }
                        composable(AppRoutes.LIKED_SONGS_SCREEN) {
                            LikedSongsScreen(viewModel = viewModel)
                        }
                        composable(
                            route = "${AppRoutes.ARTIST_SCREEN}/{${AppRoutes.ARTIST_ID_ARG}}",
                            enterTransition = { slideInHorizontally(animationSpec = tween(700), initialOffsetX = { it }) + fadeIn(animationSpec = tween(700)) },
                            exitTransition = { slideOutHorizontally(animationSpec = tween(700), targetOffsetX = { -it }) + fadeOut(animationSpec = tween(700)) },
                            popEnterTransition = { slideInHorizontally(animationSpec = tween(700), initialOffsetX = { -it }) + fadeIn(animationSpec = tween(700)) },
                            popExitTransition = { slideOutHorizontally(animationSpec = tween(700), targetOffsetX = { it }) + fadeOut(animationSpec = tween(700)) }
                        ) { backStackEntry ->
                            val artistId = backStackEntry.arguments?.getString(AppRoutes.ARTIST_ID_ARG)
                            ArtistScreen(
                                artistId = artistId,
                                navController = navController,
                                spotifyRepository = viewModel.spotifyRepository
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == SPOTIFY_AUTH_REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> viewModel.onLoginResult(response.accessToken)
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error
                }
                else -> {
                    // Handle other cases
                }
            }
        }
    }

    private fun getAuthorizationRequest(): AuthorizationRequest {
        val builder = AuthorizationRequest.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            BuildConfig.SPOTIFY_REDIRECT_URI
        )
        builder.setScopes(arrayOf("user-read-private", "user-library-read", "playlist-read-private"))
        return builder.build()
    }
}

@Composable
fun AlbumArt(uri: String?) {
    uri?.let {
        AsyncImage(
            model = it,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(180.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
    } ?: run {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color.DarkGray, MaterialTheme.shapes.medium)
        )
    }
}
