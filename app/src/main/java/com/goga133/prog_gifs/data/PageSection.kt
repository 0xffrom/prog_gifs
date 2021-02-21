package com.goga133.prog_gifs.data

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Разделы гиф-изображений.
 */
enum class PageSection(val value: String) {
    @SerializedName("random")
    RANDOM("random"),
    @SerializedName("top")
    TOP("top"),
    @SerializedName("latest")
    LATEST("latest"),
    @SerializedName("hot")
    HOT("hot");

    override fun toString(): String {
        return super.toString().toLowerCase(Locale.ROOT)
    }
}
