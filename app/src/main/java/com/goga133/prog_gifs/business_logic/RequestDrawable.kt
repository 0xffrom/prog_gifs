package com.goga133.prog_gifs.business_logic

import com.bumptech.glide.request.RequestListener

/**
 * Интерфейс для упрощённого взаимодействия с [RequestListener]
 */
interface RequestDrawable {
    fun onLoadFailed()
    fun onResourceReady()
}