package com.vz.skne.data.model

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("followers") val followers: Followers
)

data class Image(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)

data class Followers(
    @SerializedName("href") val href: String?,
    @SerializedName("total") val total: Int
)
