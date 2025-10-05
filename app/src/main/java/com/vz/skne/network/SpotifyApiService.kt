package com.vz.skne.network

import com.vz.skne.data.model.Artist
import com.vz.skne.data.model.ArtistAlbums
import com.vz.skne.data.model.ArtistTopTracks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {

    // Get Artist Details
    @GET("artists/{id}")
    suspend fun getArtist(@Path("id") artistId: String, @Header("Authorization") token: String): Response<Artist>

    // Get Artist's Top Tracks
    @GET("artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: String,
        @Header("Authorization") token: String,
        @Query("market") market: String = "US", // Specify market for track results
    ): Response<ArtistTopTracks>

    // Get Artist's Albums
    @GET("artists/{id}/albums")
    suspend fun getArtistAlbums(
        @Path("id") artistId: String,
        @Header("Authorization") token: String,
        @Query("market") market: String = "US", // Specify market for album results
        @Query("limit") limit: Int = 20, // Number of albums to retrieve
    ): Response<ArtistAlbums>

    // Get User's Liked Songs
    @GET("me/tracks")
    suspend fun getLikedSongs(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
    ): Response<com.vz.skne.data.model.LikedSongsResponse>
}
