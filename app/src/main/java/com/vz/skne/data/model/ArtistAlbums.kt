package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class ArtistAlbums(
    @SerializedName("items") val albums: List<Album>,
)

data class Album(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("total_tracks") val totalTracks: Int,
)
