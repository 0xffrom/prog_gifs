package com.goga133.prog_gifs.data

/**
 * Состояние загрузки.
 */
data class State<T>(val event: Event, val throwable: Throwable? = null, val data: T? = null)
