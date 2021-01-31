package com.goga133.fintech2021.business_logic

import com.bumptech.glide.request.RequestListener

/**
 * Интерфейс для упрощённого взаимодействия с [RequestListener]
 */
interface RequestDrawable {
    fun onLoadFailed()
    fun onResourceReady()
}