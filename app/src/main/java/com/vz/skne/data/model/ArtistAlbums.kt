package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class ArtistAlbums(
    @SerializedName("items") val albums: List<Album> // Reusing simplified Album data class
)

// Re-define or reuse Album if it's simple enough, otherwise use a more detailed one
// For simplicity here, let's assume a structure similar to what's needed for display
data class Album(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("total_tracks") val totalTracks: Int
)
