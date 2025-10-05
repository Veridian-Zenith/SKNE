package com.vz.skne.data.repository

import android.util.Base64
import com.vz.skne.data.model.Artist
import com.vz.skne.data.model.ArtistAlbums
import com.vz.skne.data.model.ArtistTopTracks
import com.vz.skne.network.AuthService
import com.vz.skne.network.SpotifyApiService

class SpotifyRepository(private val apiService: SpotifyApiService, private val authService: AuthService) {

    private var accessToken: String? = null

    fun getAccessTokenSync(): String? = accessToken

    fun setAuthToken(token: String) {
        accessToken = token
    }

    suspend fun getAndSetAccessToken(clientId: String, clientSecret: String): Result<Unit> {
        return try {
            val authString = "$clientId:$clientSecret"
            val encodedAuthString = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
            val authHeader = "Basic $encodedAuthString"

            val response = authService.getAccessToken(authHeader)
            if (response.isSuccessful && response.body() != null) {
                accessToken = response.body()!!.accessToken
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Failed to get access token: ${response.errorBody()?.string()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(RuntimeException("Unexpected error during authentication", e))
        }
    }

    suspend fun getArtist(artistId: String): Result<Artist> {
        val token = accessToken ?: return Result.failure(IllegalStateException("Access Token not set"))
        return try {
            val response = apiService.getArtist(artistId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(RuntimeException("Failed to fetch artist: ${response.errorBody()?.string()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(RuntimeException("Unexpected error fetching artist", e))
        }
    }

    suspend fun getArtistTopTracks(artistId: String, market: String = "US"): Result<ArtistTopTracks> {
        val token = accessToken ?: return Result.failure(IllegalStateException("Access Token not set"))
        return try {
            val response = apiService.getArtistTopTracks(artistId, "Bearer $token", market)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(RuntimeException("Failed to fetch top tracks: ${response.errorBody()?.string()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(RuntimeException("Unexpected error fetching top tracks", e))
        }
    }

    suspend fun getArtistAlbums(artistId: String, market: String = "US", limit: Int = 20): Result<ArtistAlbums> {
        val token = accessToken ?: return Result.failure(IllegalStateException("Access Token not set"))
        return try {
            val response = apiService.getArtistAlbums(artistId, "Bearer $token", market, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(RuntimeException("Failed to fetch albums: ${response.errorBody()?.string()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(RuntimeException("Unexpected error fetching albums", e))
        }
    }

    suspend fun getLikedSongs(limit: Int = 20): Result<List<String>> {
        val token = accessToken ?: return Result.failure(IllegalStateException("Access Token not set"))
        return try {
            val response = apiService.getLikedSongs("Bearer $token", limit)
            if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.items.map { trackItem ->
                    "${trackItem.track.artists.joinToString(", ") { it.name }} - ${trackItem.track.name}"
                }
                Result.success(songs)
            } else {
                Result.failure(RuntimeException("Failed to fetch liked songs: ${response.errorBody()?.string()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(RuntimeException("Unexpected error fetching liked songs", e))
        }
    }
}
