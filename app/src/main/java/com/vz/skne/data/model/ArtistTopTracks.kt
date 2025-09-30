package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class ArtistTopTracks(
    @SerializedName("tracks") val tracks: List<Track>
)

data class Track(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("album") val album: AlbumSimplified,
    @SerializedName("artists") val artists: List<ArtistSimplified>,
    @SerializedName("duration_ms") val durationMs: Int,
    @SerializedName("preview_url") val previewUrl: String? // URL for a 30-second preview
)

data class AlbumSimplified(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<Image>
)

data class ArtistSimplified(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
