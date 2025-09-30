package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class LikedSongsResponse(
    @SerializedName("items") val items: List<TrackItem>
)

data class TrackItem(
    @SerializedName("track") val track: Track
)

data class Track(
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<ArtistInfo>,
    @SerializedName("album") val album: Album
)

data class ArtistInfo(
    @SerializedName("name") val name: String
)

data class Album(
    @SerializedName("images") val images: List<Image>
)

data class Image(
    @SerializedName("url") val url: String
)

// Lyrics data models
data class LyricsResponse(
    @SerializedName("lyrics") val lyrics: String
)

data class LyricsError(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String?
)
