package com.goga133.prog_gifs.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Gif - изображение
 */
data class Gif(
    val id: Int,
    val description: String,
    val votes: Int,
    val author: String,
    val date: Date,
    @SerializedName("gifURL")
    val gifUrl : String,
    val gifSize : String,
    @SerializedName("previewURL")
    val previewUrl : String,
    val width : Int,
    val height : Int,
    val commentsCount : Int,
    val fileSize : Int
) : Serializable
