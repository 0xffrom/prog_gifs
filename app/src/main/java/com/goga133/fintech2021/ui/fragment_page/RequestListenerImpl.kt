package com.goga133.fintech2021.ui.fragment_page

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.goga133.fintech2021.business_logic.RequestDrawable

/**
 * Имплиментация [RequestListener]. Каждый метод [RequestListener] вызывает аналогичный метод
 * из [RequestDrawable].
 */
class RequestListenerImpl(private val requestDrawable: RequestDrawable) : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        requestDrawable.onLoadFailed()

        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        requestDrawable.onResourceReady()

        return false
    }
}