package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class LikedSongsResponse(
    @SerializedName("items") val items: List<TrackItem>,
)

data class TrackItem(
    @SerializedName("track") val track: LikedTrack,
)

data class LikedTrack(
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<ArtistInfo>,
    @SerializedName("album") val album: LikedAlbum,
)

data class ArtistInfo(
    @SerializedName("name") val name: String,
)

data class LikedAlbum(
    @SerializedName("images") val images: List<LikedImage>,
)

data class LikedImage(
    @SerializedName("url") val url: String,
)

// Lyrics data models
data class LyricsResponse(
    @SerializedName("lyrics") val lyrics: String,
)

data class LyricsError(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String?,
)
